package fuzs.combatnouveau.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ClientConfig;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public class RenderOffhandItemHandler {

    public static EventResult onRenderOffHand(ItemInHandRenderer itemInHandRenderer, InteractionHand interactionHand, AbstractClientPlayer player, HumanoidArm humanoidArm, ItemStack itemStack, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, float partialTick, float interpolatedPitch, float swingProgress, float equipProgress) {
        if (!itemStack.isEmpty() &&
                CombatNouveau.CONFIG.get(ClientConfig.class).hiddenOffhandItems.contains(itemStack.getItem())) {
            if (!player.isUsingItem() || player.getUsedItemHand() != InteractionHand.OFF_HAND ||
                    itemStack.getItem() instanceof ShieldItem &&
                            CombatNouveau.CONFIG.get(ClientConfig.class).shieldIndicator) {
                return EventResult.INTERRUPT;
            }
        }
        return EventResult.PASS;
    }
}
