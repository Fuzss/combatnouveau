package fuzs.combatnouveau.client;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class CombatNouveauFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(CombatNouveau.MOD_ID, CombatNouveauClient::new);
    }
}
