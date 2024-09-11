package fuzs.combatnouveau.neoforge.client;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.client.CombatNouveauClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = CombatNouveau.MOD_ID, dist = Dist.CLIENT)
public class CombatNouveauNeoForgeClient {

    public CombatNouveauNeoForgeClient() {
        ClientModConstructor.construct(CombatNouveau.MOD_ID, CombatNouveauClient::new);
    }
}
