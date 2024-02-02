package fuzs.combatnouveau.fabric.core;

import fuzs.combatnouveau.core.CommonAbstractions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class FabricAbstractions implements CommonAbstractions {

    @Nullable
    @Override
    public Attribute getAttackRangeAttribute() {
//        return ReachEntityAttributes.ATTACK_RANGE;
        return null;
    }

    @Override
    public AABB getSweepHitBox(Player player, Entity target) {
        return target.getBoundingBox().inflate(1.0, 0.25, 1.0);
    }
}
