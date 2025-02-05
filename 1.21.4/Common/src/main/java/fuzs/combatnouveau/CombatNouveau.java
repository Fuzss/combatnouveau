package fuzs.combatnouveau;

import fuzs.combatnouveau.config.ClientConfig;
import fuzs.combatnouveau.config.CommonConfig;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.combatnouveau.data.DynamicDatapackRegistriesProvider;
import fuzs.combatnouveau.handler.AttackAttributeHandler;
import fuzs.combatnouveau.handler.ClassicCombatHandler;
import fuzs.combatnouveau.handler.CombatTestHandler;
import fuzs.combatnouveau.network.client.ServerboundSweepAttackMessage;
import fuzs.combatnouveau.network.client.ServerboundSwingArmMessage;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.ComputeItemAttributeModifiersCallback;
import fuzs.puzzleslib.api.event.v1.FinalizeItemComponentsCallback;
import fuzs.puzzleslib.api.event.v1.entity.ProjectileImpactCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingHurtCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingKnockBackCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.ShieldBlockCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.UseItemEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerTickEvents;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import fuzs.puzzleslib.api.resources.v1.DynamicPackResources;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombatNouveau implements ModConstructor {
    public static final String MOD_ID = "combatnouveau";
    public static final String MOD_NAME = "Combat Nouveau";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandler NETWORK = NetworkHandler.builder(MOD_ID)
            .registerServerbound(ServerboundSweepAttackMessage.class)
            .registerServerbound(ServerboundSwingArmMessage.class);
    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID)
            .client(ClientConfig.class)
            .common(CommonConfig.class)
            .server(ServerConfig.class);

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ComputeItemAttributeModifiersCallback.EVENT.register(AttackAttributeHandler::onComputeItemAttributeModifiers);
        FinalizeItemComponentsCallback.EVENT.register(AttackAttributeHandler::onFinalizeItemComponents);
        LivingKnockBackCallback.EVENT.register(ClassicCombatHandler::onLivingKnockBack);
        ProjectileImpactCallback.EVENT.register(ClassicCombatHandler::onProjectileImpact);
        PlayerInteractEvents.USE_ITEM.register(CombatTestHandler::onUseItem);
        UseItemEvents.START.register(CombatTestHandler::onUseItemStart);
        PlayerTickEvents.START.register(CombatTestHandler::onStartPlayerTick);
        LivingHurtCallback.EVENT.register(CombatTestHandler::onLivingHurt);
        ShieldBlockCallback.EVENT.register(CombatTestHandler::onShieldBlock);
    }

    @Override
    public void onAddDataPackFinders(PackRepositorySourcesContext context) {
        // need this here so the game does not complain about experimental settings when the config option is disabled
        if (!CONFIG.get(CommonConfig.class).halveSweepingDamage) return;
        context.addRepositorySource(PackResourcesHelper.buildServerPack(id("halved_sweeping_damage"),
                DynamicPackResources.create(DynamicDatapackRegistriesProvider::new),
                true));
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, path);
    }
}
