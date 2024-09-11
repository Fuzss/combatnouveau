package fuzs.combatnouveau.client;

import fuzs.combatnouveau.client.handler.AttributesTooltipHandler;
import fuzs.combatnouveau.client.handler.AutoAttackHandler;
import fuzs.combatnouveau.client.handler.RenderOffhandItemHandler;
import fuzs.combatnouveau.client.handler.ShieldIndicatorHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.player.InteractionInputEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ItemTooltipCallback;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderGuiElementEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderHandCallback;

public class CombatNouveauClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerHandlers();
    }

    private static void registerHandlers() {
        RenderGuiElementEvents.before(RenderGuiElementEvents.CROSSHAIR).register(ShieldIndicatorHandler::onBeforeRenderGuiElement);
        RenderGuiElementEvents.after(RenderGuiElementEvents.CROSSHAIR).register((minecraft, guiGraphics, tickDelta, screenWidth, screenHeight) -> ShieldIndicatorHandler.onAfterRenderGuiElement(RenderGuiElementEvents.CROSSHAIR, minecraft, guiGraphics, tickDelta, screenWidth, screenHeight));
        RenderGuiElementEvents.before(RenderGuiElementEvents.HOTBAR).register(ShieldIndicatorHandler::onBeforeRenderGuiElement);
        RenderGuiElementEvents.after(RenderGuiElementEvents.HOTBAR).register((minecraft, guiGraphics, tickDelta, screenWidth, screenHeight) -> ShieldIndicatorHandler.onAfterRenderGuiElement(RenderGuiElementEvents.HOTBAR, minecraft, guiGraphics, tickDelta, screenWidth, screenHeight));
        InteractionInputEvents.ATTACK.register(AutoAttackHandler::onAttackInteraction);
        ClientTickEvents.START.register(AutoAttackHandler::onStartTick);
        RenderHandCallback.EVENT.register(RenderOffhandItemHandler::onRenderHand);
        ItemTooltipCallback.EVENT.register(AttributesTooltipHandler::onItemTooltip);
    }
}
