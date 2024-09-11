package fuzs.combatnouveau.mixin;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ServerConfig;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "blockedByShield", at = @At("HEAD"), cancellable = true)
    protected void blockedByShield(LivingEntity target, CallbackInfo callback) {
        if (CombatNouveau.CONFIG.get(ServerConfig.class).shieldKnockback == ServerConfig.ShieldKnockback.NONE) return;
        double knockBackStrength;
        if (CombatNouveau.CONFIG.get(ServerConfig.class).shieldKnockback == ServerConfig.ShieldKnockback.VARIABLE) {
            int variableShieldKnockbackDelay = CombatNouveau.CONFIG.get(ServerConfig.class).variableShieldKnockbackDelay;
            if (!CombatNouveau.CONFIG.get(ServerConfig.class).removeShieldDelay) variableShieldKnockbackDelay += 5;
            knockBackStrength = (target.getUseItem().getUseDuration() - target.getUseItemRemainingTicks()) / (double) variableShieldKnockbackDelay;
            knockBackStrength = 1.0 - Mth.clamp(knockBackStrength, 0.0, 1.0);
            knockBackStrength += 0.5;
        } else {
            knockBackStrength = 0.5;
        }
        this.knockback(knockBackStrength, target.getX() - this.getX(), target.getZ() - this.getZ());
        callback.cancel();
    }

    @Shadow
    public abstract void knockback(double p_147241_, double p_147242_, double p_147243_);

    @Inject(method = "isDamageSourceBlocked", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;getSourcePosition()Lnet/minecraft/world/phys/Vec3;", shift = At.Shift.BEFORE), cancellable = true)
    public void isDamageSourceBlocked(DamageSource damageSource, CallbackInfoReturnable<Boolean> callback) {
        Vec3 sourcePosition = damageSource.getSourcePosition();
        if (sourcePosition != null && !damageSource.is(DamageTypeTags.IS_PROJECTILE)) {
            Vec3 viewVector = this.getViewVector(1.0F);
            Vec3 vec3 = sourcePosition.vectorTo(this.position()).normalize();
            vec3 = new Vec3(vec3.x, 0.0, vec3.z);
            double protectionArc = -Math.cos(CombatNouveau.CONFIG.get(ServerConfig.class).shieldProtectionArc * Math.PI * 0.5 / 180.0);
            callback.setReturnValue(vec3.dot(viewVector) < protectionArc);
        }
    }
}
