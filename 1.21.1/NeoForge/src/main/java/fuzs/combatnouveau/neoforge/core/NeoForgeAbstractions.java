package fuzs.combatnouveau.neoforge.core;

import fuzs.combatnouveau.core.CommonAbstractions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForgeMod;

public class NeoForgeAbstractions implements CommonAbstractions {

    @Override
    public Attribute getAttackRangeAttribute() {
        return NeoForgeMod.ENTITY_REACH.value();
    }

    @Override
    public AABB getSweepHitBox(Player player, Entity target) {
        return player.getItemInHand(InteractionHand.MAIN_HAND).getSweepHitBox(player, target);
    }
}
