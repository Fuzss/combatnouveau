package fuzs.combatnouveau.forge.core;

import fuzs.combatnouveau.core.CommonAbstractions;
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
    public AABB getSweepHitBox(Player player, Entity target) {
        return player.getItemInHand(InteractionHand.MAIN_HAND).getSweepHitBox(player, target);
    }
}
