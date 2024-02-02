package fuzs.combatnouveau.neoforge.client;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.client.CombatNouveauClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = CombatNouveau.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CombatNouveauNeoForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(CombatNouveau.MOD_ID, CombatNouveauClient::new);
    }
}
