package fuzs.combatnouveau.client.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ClientConfig;
import fuzs.puzzleslib.api.client.event.v1.RenderGuiElementEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.Nullable;

public class ShieldIndicatorHandler {
    public static final ResourceLocation GUI_ICONS_LOCATION = CombatNouveau.id("textures/gui/icons.png");

    @Nullable
    private static AttackIndicatorStatus attackIndicator = null;

    public static EventResult onBeforeRenderGuiElement(Minecraft minecraft, PoseStack poseStack, float tickDelta, int screenWidth, int screenHeight) {
        if (!CombatNouveau.CONFIG.get(ClientConfig.class).shieldIndicator) return EventResult.PASS;
        if (attackIndicator == null && minecraft.player.isBlocking()) {
            attackIndicator = minecraft.options.attackIndicator;
            minecraft.options.attackIndicator = AttackIndicatorStatus.OFF;
        }
        return EventResult.PASS;
    }

    public static void onAfterRenderGuiElement(RenderGuiElementEvents.GuiOverlay guiOverlay, Minecraft minecraft, PoseStack poseStack, float tickDelta, int screenWidth, int screenHeight) {
        // reset to old value; don't just leave this disabled as it'll change the vanilla setting permanently in options.txt, which no mod should do imo
        if (attackIndicator != null) {
            minecraft.options.attackIndicator = attackIndicator;
            attackIndicator = null;
            if (guiOverlay == RenderGuiElementEvents.CROSSHAIR && minecraft.options.attackIndicator == AttackIndicatorStatus.CROSSHAIR) {
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                int posX = screenWidth / 2 - 8;
                int posY = screenHeight / 2 - 7 + 16;
                RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                GuiComponent.blit(poseStack, posX, posY, 70, 0, 16, 14, 256, 256);
                RenderSystem.defaultBlendFunc();
            } else if (guiOverlay == RenderGuiElementEvents.HOTBAR && minecraft.options.attackIndicator == AttackIndicatorStatus.HOTBAR) {
                RenderSystem.enableBlend();
                int posX;
                if (minecraft.player.getMainArm() == HumanoidArm.LEFT) {
                    posX = screenWidth / 2 - 91 - 22;
                } else {
                    posX = screenWidth / 2 + 91 + 6;
                }
                int posY = screenHeight - 20;
                RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                GuiComponent.blit(poseStack, posX, posY, 18, 0, 18, 18, 256, 256);
                RenderSystem.disableBlend();
            }
        }
    }
}
