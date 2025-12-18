package fuzs.combatnouveau.neoforge.services;

import fuzs.combatnouveau.services.CommonAbstractions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public final class NeoForgeAbstractions implements CommonAbstractions {

    @Override
    public AABB getSweepHitBox(Player player, Entity target) {
        return player.getItemInHand(InteractionHand.MAIN_HAND).getSweepHitBox(player, target);
    }
}
