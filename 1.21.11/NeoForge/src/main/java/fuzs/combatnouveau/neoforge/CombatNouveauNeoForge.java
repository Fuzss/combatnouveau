package fuzs.combatnouveau.neoforge;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.neoforged.fml.common.Mod;

@Mod(CombatNouveau.MOD_ID)
public class CombatNouveauNeoForge {

    public CombatNouveauNeoForge() {
        ModConstructor.construct(CombatNouveau.MOD_ID, CombatNouveau::new);
    }
}
