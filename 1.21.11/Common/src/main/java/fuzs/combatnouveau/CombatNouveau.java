package fuzs.combatnouveau;

import fuzs.combatnouveau.config.ClientConfig;
import fuzs.combatnouveau.config.CommonConfig;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.combatnouveau.handler.ItemComponentsHandler;
import fuzs.combatnouveau.handler.ClassicCombatHandler;
import fuzs.combatnouveau.handler.CombatTestHandler;
import fuzs.combatnouveau.init.ModRegistry;
import fuzs.combatnouveau.network.client.ServerboundSweepAttackMessage;
import fuzs.combatnouveau.network.client.ServerboundSwingArmMessage;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.EntityAttributesContext;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.api.event.v1.FinalizeItemComponentsCallback;
import fuzs.puzzleslib.api.event.v1.entity.ProjectileImpactCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingHurtCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingKnockBackCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.ShieldBlockCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombatNouveau implements ModConstructor {
    public static final String MOD_ID = "combatnouveau";
    public static final String MOD_NAME = "Combat Nouveau";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID)
            .client(ClientConfig.class)
            .common(CommonConfig.class)
            .server(ServerConfig.class);
    public static final Identifier WEAK_SWEEPING_EDGE_ID = id("weak_sweeping_edge");

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        FinalizeItemComponentsCallback.EVENT.register(ItemComponentsHandler::onFinalizeItemComponents);
        LivingKnockBackCallback.EVENT.register(ClassicCombatHandler::onLivingKnockBack);
        ProjectileImpactCallback.EVENT.register(ClassicCombatHandler::onProjectileImpact);
        PlayerTickEvents.START.register(CombatTestHandler::onStartPlayerTick);
        LivingHurtCallback.EVENT.register(CombatTestHandler::onLivingHurt);
        ShieldBlockCallback.EVENT.register(CombatTestHandler::onShieldBlock);
    }

    @Override
    public void onRegisterPayloadTypes(PayloadTypesContext context) {
        context.playToServer(ServerboundSweepAttackMessage.class, ServerboundSweepAttackMessage.STREAM_CODEC);
        context.playToServer(ServerboundSwingArmMessage.class, ServerboundSwingArmMessage.STREAM_CODEC);
    }

    @Override
    public void onAddDataPackFinders(PackRepositorySourcesContext context) {
        context.registerBuiltInPack(WEAK_SWEEPING_EDGE_ID, Component.literal("Halve Sweeping Damage"), false);
    }

    @Override
    public void onRegisterEntityAttributes(EntityAttributesContext context) {
        if (CONFIG.get(CommonConfig.class).doublePlayerAttackStrength) {
            context.registerAttribute(EntityType.PLAYER, Attributes.ATTACK_DAMAGE, 2.0);
        }
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
