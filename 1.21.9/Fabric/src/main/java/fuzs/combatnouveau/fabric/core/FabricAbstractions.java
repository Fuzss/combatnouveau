package fuzs.combatnouveau.fabric.core;

import fuzs.combatnouveau.core.CommonAbstractions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class FabricAbstractions implements CommonAbstractions {

    @Override
    public AABB getSweepHitBox(Player player, Entity target) {
        return target.getBoundingBox().inflate(1.0, 0.25, 1.0);
    }
}
