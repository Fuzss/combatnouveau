package fuzs.combatnouveau.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ClientConfig;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public class RenderOffhandItemHandler {

    public static EventResult onRenderHand(Player player, InteractionHand hand, ItemStack stack, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, float partialTick, float interpolatedPitch, float swingProgress, float equipProgress) {
        if (hand == InteractionHand.OFF_HAND) {
            if (!stack.isEmpty() && CombatNouveau.CONFIG.get(ClientConfig.class).hiddenOffhandItems.contains(stack.getItem())) {
                if (!player.isUsingItem() || player.getUsedItemHand() != InteractionHand.OFF_HAND || stack.getItem() instanceof ShieldItem && CombatNouveau.CONFIG.get(ClientConfig.class).shieldIndicator) {
                    return EventResult.INTERRUPT;
                }
            }
        }
        return EventResult.PASS;
    }
}
