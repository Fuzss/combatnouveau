package fuzs.combatnouveau.core;

import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderHelper.load(CommonAbstractions.class);

    default Holder<Attribute> getAttackRangeAttribute() {
        return Attributes.ENTITY_INTERACTION_RANGE;
    }

    AABB getSweepHitBox(Player player, Entity target);
}
