package fuzs.combatnouveau.client;

import fuzs.combatnouveau.client.handler.AttackAirHandler;
import fuzs.combatnouveau.client.handler.AttributesTooltipHandler;
import fuzs.combatnouveau.client.handler.FirstPersonOffhandHandler;
import fuzs.combatnouveau.client.handler.ShieldIndicatorHandler;
import fuzs.combatnouveau.mixin.client.accessor.MultiPlayerGameModeAccessor;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.*;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

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
        ItemTooltipCallback.EVENT.register(AttributesTooltipHandler::onItemTooltip);
        InteractionInputEvents.ATTACK.register((minecraft, player) -> {
            if (minecraft.hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult) minecraft.hitResult).getEntity().isAlive()) {
                ((MultiPlayerGameModeAccessor) minecraft.gameMode).combatnouveau$setDestroyDelay(5);
            }
            return EventResult.PASS;
        });
    }
}
