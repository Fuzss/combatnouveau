package fuzs.combatnouveau.core;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class FabricAbstractions implements CommonAbstractions {

    @Override
    public Attribute getAttackRangeAttribute() {
        return ReachEntityAttributes.ATTACK_RANGE;
    }

    @Override
    public double getCurrentAttackReach(Player player, boolean hasFarPickRange) {
        return CommonAbstractions.super.getCurrentAttackReach(player, hasFarPickRange) + 3.0;
    }

    @Override
    public AABB getSweepHitBox(Player player, Entity target) {
        return target.getBoundingBox().inflate(1.0, 0.25, 1.0);
    }
}
