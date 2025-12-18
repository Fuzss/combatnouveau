package fuzs.combatnouveau.client.handler;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ClientConfig;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import org.jspecify.annotations.Nullable;

public class ShieldIndicatorHandler {
    public static final Identifier GUI_ICONS_LOCATION = CombatNouveau.id("textures/gui/icons.png");

    @Nullable
    private static AttackIndicatorStatus attackIndicator = null;

    public static void onBeforeRenderGui(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!CombatNouveau.CONFIG.get(ClientConfig.class).shieldIndicator) return;
        if (attackIndicator == null && Minecraft.getInstance().player.isBlocking()) {
            Options options = Minecraft.getInstance().options;
            attackIndicator = options.attackIndicator().get();
            options.attackIndicator().set(AttackIndicatorStatus.OFF);
        }
    }

    public static void onAfterRenderGui(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (attackIndicator != null) {
            Minecraft.getInstance().options.attackIndicator().set(attackIndicator);
            attackIndicator = null;
        }
    }

    public static void renderCrosshairBlockingIndicator(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (attackIndicator == AttackIndicatorStatus.CROSSHAIR) {
            if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                int posX = guiGraphics.guiWidth() / 2 - 8;
                int posY = guiGraphics.guiHeight() / 2 - 7 + 16;
                guiGraphics.blit(RenderPipelines.CROSSHAIR, GUI_ICONS_LOCATION, posX, posY, 70, 0, 16, 14, 256, 256);
            }
        }
    }

    public static void renderHotbarBlockingIndicator(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (attackIndicator == AttackIndicatorStatus.HOTBAR) {
            int posX;
            if (Minecraft.getInstance().player.getMainArm() == HumanoidArm.LEFT) {
                posX = guiGraphics.guiWidth() / 2 - 91 - 22;
            } else {
                posX = guiGraphics.guiWidth() / 2 + 91 + 6;
            }
            int posY = guiGraphics.guiHeight() - 20;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_ICONS_LOCATION, posX, posY, 18, 0, 18, 18, 256, 256);
        }
    }
}
