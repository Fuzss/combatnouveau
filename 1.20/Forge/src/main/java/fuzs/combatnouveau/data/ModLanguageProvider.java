package fuzs.combatnouveau.data;

import fuzs.puzzleslib.api.data.v1.AbstractLanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void addTranslations() {
        this.add("attribute.name.generic.reach-entity-attributes.attack_range", "Attack Reach");
        this.add("forge.entity_reach", "Attack Reach");
    }
}
