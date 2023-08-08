package fuzs.combatnouveau.mixin.client;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.client.helper.GameRendererPickHelper;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.combatnouveau.core.CommonAbstractions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Nullable
    @Unique
    private HitResult combatnouveau$originalBlockHitResult;

    @Inject(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getEyePosition(F)Lnet/minecraft/world/phys/Vec3;"))
    public void pick$0(float partialTicks, CallbackInfo callback) {
        if (!CombatNouveau.CONFIG.get(ServerConfig.class).swingThroughGrass) return;
        this.combatnouveau$originalBlockHitResult = this.minecraft.hitResult;
        Entity entity = this.minecraft.getCameraEntity();
        double pickRange = CommonAbstractions.INSTANCE.adjustPickRange(this.minecraft.gameMode.getPickRange(), this.minecraft.player);
        this.minecraft.hitResult = GameRendererPickHelper.pick(entity, pickRange, partialTicks);
    }

    @Inject(method = "pick", at = @At("TAIL"))
    public void pick$1(float partialTicks, CallbackInfo callback) {
        if (this.combatnouveau$originalBlockHitResult != null) {
            if (this.minecraft.hitResult != null && this.minecraft.hitResult.getType() != HitResult.Type.ENTITY) {
                Vec3 eyePosition = this.minecraft.getCameraEntity().getEyePosition(partialTicks);
                if (this.combatnouveau$originalBlockHitResult.getLocation().distanceToSqr(eyePosition) < this.minecraft.hitResult.getLocation().distanceToSqr(eyePosition)) {
                    this.minecraft.hitResult = this.combatnouveau$originalBlockHitResult;
                }
            }
            this.combatnouveau$originalBlockHitResult = null;
        }
    }

    @Inject(method = {"method_18144", "lambda$pick$61"}, at = @At("HEAD"), cancellable = true)
    private static void isPickable(Entity entity, CallbackInfoReturnable<Boolean> callback) {
        if (!entity.isAlive()) callback.setReturnValue(false);
    }
}
