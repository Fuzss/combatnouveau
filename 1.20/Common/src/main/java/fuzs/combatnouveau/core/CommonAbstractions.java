package fuzs.combatnouveau.core;

import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderHelper.load(CommonAbstractions.class);

    Attribute getAttackRangeAttribute();

    default double getCurrentAttackReach(Player player, boolean hasFarPickRange) {
        // TODO check this
        double attackReach = player.getAttribute(CommonAbstractions.INSTANCE.getAttackRangeAttribute()).getValue();
        if (hasFarPickRange) {
            attackReach += 0.5;
        }
        if (player.isCrouching()) {
            attackReach -= 0.5;
        }
        return attackReach;
    }

    AABB getSweepHitBox(Player player, Entity target);
}
