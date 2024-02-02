package fuzs.combatnouveau.fabric;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class CombatNouveauFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(CombatNouveau.MOD_ID, CombatNouveau::new);
    }
}
