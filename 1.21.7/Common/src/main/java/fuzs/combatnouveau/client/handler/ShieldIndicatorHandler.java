package fuzs.combatnouveau.client.handler;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ClientConfig;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.Nullable;

public class ShieldIndicatorHandler {
    public static final ResourceLocation GUI_ICONS_LOCATION = CombatNouveau.id("textures/gui/icons.png");

    @Nullable
    private static AttackIndicatorStatus attackIndicator = null;

    public static void onBeforeRenderGui(Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!CombatNouveau.CONFIG.get(ClientConfig.class).shieldIndicator) return;
        if (attackIndicator == null && gui.minecraft.player.isBlocking()) {
            attackIndicator = gui.minecraft.options.attackIndicator().get();
            gui.minecraft.options.attackIndicator().set(AttackIndicatorStatus.OFF);
        }
    }

    public static void onAfterRenderGui(Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (attackIndicator != null) {
            gui.minecraft.options.attackIndicator().set(attackIndicator);
            attackIndicator = null;
        }
    }

    public static RenderGuiLayerEvents.After onAfterRenderGuiLayer(ResourceLocation resourceLocation) {
        return (Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
            onAfterRenderGuiLayer(resourceLocation, gui, guiGraphics, deltaTracker);
        };
    }

    public static void onAfterRenderGuiLayer(ResourceLocation resourceLocation, Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        // reset to old value; don't just leave this disabled as it'll change the vanilla setting permanently in options.txt, which no mod should do imo
        if (attackIndicator != null) {
            int screenWidth = guiGraphics.guiWidth();
            int screenHeight = guiGraphics.guiHeight();
            if (resourceLocation.equals(RenderGuiLayerEvents.CROSSHAIR) &&
                    attackIndicator == AttackIndicatorStatus.CROSSHAIR) {
                if (gui.minecraft.options.getCameraType().isFirstPerson()) {
                    int posX = screenWidth / 2 - 8;
                    int posY = screenHeight / 2 - 7 + 16;
                    guiGraphics.blit(RenderType::crosshair, GUI_ICONS_LOCATION, posX, posY, 70, 0, 16, 14, 256, 256);
                }
            } else if (resourceLocation.equals(RenderGuiLayerEvents.HOTBAR) &&
                    attackIndicator == AttackIndicatorStatus.HOTBAR) {
                int posX;
                if (gui.minecraft.player.getMainArm() == HumanoidArm.LEFT) {
                    posX = screenWidth / 2 - 91 - 22;
                } else {
                    posX = screenWidth / 2 + 91 + 6;
                }
                int posY = screenHeight - 20;
                guiGraphics.blit(RenderType::guiTextured, GUI_ICONS_LOCATION, posX, posY, 18, 0, 18, 18, 256, 256);
            }
        }
    }
}
