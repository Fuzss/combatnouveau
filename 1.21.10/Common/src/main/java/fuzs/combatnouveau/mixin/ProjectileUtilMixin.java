package fuzs.combatnouveau.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ServerConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ProjectileUtil.class)
abstract class ProjectileUtilMixin {

    @ModifyVariable(method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
            at = @At("STORE"),
            ordinal = 1)
    private static AABB getEntityHitResult(AABB aabb, @Local(ordinal = 2) Entity entity) {
        double minHitboxSize = CombatNouveau.CONFIG.get(ServerConfig.class).minHitboxSize;
        if (minHitboxSize == 0.0) {
            return aabb;
        } else if (entity instanceof LivingEntity && (aabb.getXsize() < minHitboxSize || aabb.getYsize() < minHitboxSize
                || aabb.getZsize() < minHitboxSize)) {
            return aabb.inflate(Math.max(0.0, minHitboxSize - aabb.getXsize()) / 2.0,
                    Math.max(0.0, minHitboxSize - aabb.getYsize()) / 2.0,
                    Math.max(0.0, minHitboxSize - aabb.getZsize()) / 2.0);
        } else {
            return aabb;
        }
    }
}
