package fuzs.combatnouveau.handler;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedDouble;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ClassicCombatHandler {

    public static EventResult onProjectileImpact(Projectile projectile, HitResult hitResult) {
        if (!CombatNouveau.CONFIG.get(ServerConfig.class).weakAttacksKnockBackPlayers) return EventResult.PASS;
        if (hitResult.getType() == HitResult.Type.ENTITY && projectile.getOwner() == null) {
            // enable knockback for item projectiles fired from dispensers by making shooter not be null
            // something similar is already done in AbstractArrowEntity::onEntityHit to account for arrows fired from dispensers
            projectile.setOwner(projectile);
        }
        return EventResult.PASS;
    }

    public static EventResult onLivingKnockBack(LivingEntity entity, DefaultedDouble strength, DefaultedDouble ratioX, DefaultedDouble ratioZ) {
        if (!CombatNouveau.CONFIG.get(ServerConfig.class).upwardsKnockback) return EventResult.PASS;
        if (!entity.isOnGround() && !entity.isInWater()) {
            strength.mapDouble(s -> s * (1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
            final Vec3 deltaMovement = entity.getDeltaMovement();
            entity.setDeltaMovement(deltaMovement.x, Math.min(0.4, deltaMovement.y / 2.0D + strength.getAsDouble()), deltaMovement.x);
        }
        return EventResult.PASS;
    }
}
