package fuzs.combatnouveau;

import fuzs.combatnouveau.config.ClientConfig;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.combatnouveau.handler.AttackAttributeHandler;
import fuzs.combatnouveau.handler.CombatTestHandler;
import fuzs.combatnouveau.network.client.ServerboundSweepAttackMessage;
import fuzs.combatnouveau.network.client.ServerboundSwingArmMessage;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.entity.living.ItemAttributeModifiersCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingHurtCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.UseItemEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerTickEvents;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombatNouveau implements ModConstructor {
    public static final String MOD_ID = "combatnouveau";
    public static final String MOD_NAME = "Combat Nouveau";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandlerV3 NETWORK = NetworkHandlerV3.builder(MOD_ID).registerServerbound(ServerboundSweepAttackMessage.class).registerServerbound(ServerboundSwingArmMessage.class);
    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).client(ClientConfig.class).server(ServerConfig.class);

    @Override
    public void onConstructMod() {
        regsiterHandlers();
    }

    private static void regsiterHandlers() {
        ItemAttributeModifiersCallback.EVENT.register(AttackAttributeHandler::onItemAttributeModifiers);
        PlayerInteractEvents.USE_ITEM.register(CombatTestHandler::onUseItem);
        UseItemEvents.START.register(CombatTestHandler::onUseItemStart);
        PlayerTickEvents.START.register(CombatTestHandler::onStartPlayerTick);
        LivingHurtCallback.EVENT.register(CombatTestHandler::onLivingHurt);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
