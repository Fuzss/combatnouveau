package fuzs.combatnouveau.client;

import fuzs.combatnouveau.client.handler.AttackAirHandler;
import fuzs.combatnouveau.client.handler.FirstPersonOffhandHandler;
import fuzs.combatnouveau.client.handler.ShieldIndicatorHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.InteractionInputEvents;
import fuzs.puzzleslib.api.client.event.v1.RenderGuiElementEvents;
import fuzs.puzzleslib.api.client.event.v1.RenderHandCallback;

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
        InteractionInputEvents.ATTACK.register(AttackAirHandler::onAttackInteraction);
        ClientTickEvents.END.register(AttackAirHandler::onEndTick);
        RenderHandCallback.EVENT.register(FirstPersonOffhandHandler::onRenderHand);
    }
}
