package fuzs.combatnouveau.neoforge;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.data.ModDatapackRegistriesProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.minecraft.server.packs.PackType;
import net.neoforged.fml.common.Mod;

@Mod(CombatNouveau.MOD_ID)
public class CombatNouveauNeoForge {

    public CombatNouveauNeoForge() {
        ModConstructor.construct(CombatNouveau.MOD_ID, CombatNouveau::new);
        DataProviderHelper.registerDataProviders(CombatNouveau.WEAK_SWEEPING_EDGE_ID,
                PackType.SERVER_DATA,
                ModDatapackRegistriesProvider::new);
    }
}
