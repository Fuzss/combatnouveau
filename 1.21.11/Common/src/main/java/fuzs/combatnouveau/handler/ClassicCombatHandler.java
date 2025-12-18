package fuzs.combatnouveau.handler;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableDouble;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ClassicCombatHandler {

    public static EventResult onProjectileImpact(Projectile projectile, HitResult hitResult) {
        if (!CombatNouveau.CONFIG.get(ServerConfig.class).weakAttackKnockBack) return EventResult.PASS;
        if (hitResult.getType() == HitResult.Type.ENTITY && projectile.getOwner() == null) {
            // enable knockback for item projectiles fired from dispensers by making shooter not be null
            // something similar is already done in AbstractArrowEntity::onEntityHit to account for arrows fired from dispensers
            projectile.setOwner(projectile);
        }
        return EventResult.PASS;
    }

    public static EventResult onLivingKnockBack(LivingEntity livingEntity, MutableDouble knockbackStrength, MutableDouble ratioX, MutableDouble ratioZ) {
        if (!CombatNouveau.CONFIG.get(ServerConfig.class).upwardsKnockback) return EventResult.PASS;
        if (!livingEntity.onGround() && !livingEntity.isInWater()) {
            knockbackStrength.mapDouble((double v) -> v *
                    (1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
            final Vec3 deltaMovement = livingEntity.getDeltaMovement();
            livingEntity.setDeltaMovement(deltaMovement.x,
                    Math.min(0.4, deltaMovement.y / 2.0D + knockbackStrength.getAsDouble()),
                    deltaMovement.x);
        }
        return EventResult.PASS;
    }
}
