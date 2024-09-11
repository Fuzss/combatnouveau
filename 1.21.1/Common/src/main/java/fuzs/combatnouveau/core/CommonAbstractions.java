package fuzs.combatnouveau.core;

import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderHelper.load(CommonAbstractions.class);

    @Nullable
    Attribute getAttackRangeAttribute();

    AABB getSweepHitBox(Player player, Entity target);
}
