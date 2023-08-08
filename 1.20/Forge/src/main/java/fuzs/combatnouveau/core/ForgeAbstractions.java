package fuzs.combatnouveau.core;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;

public class ForgeAbstractions implements CommonAbstractions {

    @Override
    public Attribute getAttackRangeAttribute() {
        return ForgeMod.ENTITY_REACH.get();
    }

    @Override
    public double adjustPickRange(double pickRange, Player player) {
        return Math.max(CommonAbstractions.super.adjustPickRange(pickRange, player), player.getEntityReach());
    }

    @Override
    public AABB getSweepHitBox(Player player, Entity target) {
        return player.getItemInHand(InteractionHand.MAIN_HAND).getSweepHitBox(player, target);
    }
}
