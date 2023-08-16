package fuzs.combatnouveau.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public class ClientConfig implements ConfigCore {
    @Config(description = "Improved arm swing animation to emphasize the rhythm of the attacks from combat test snapshots.")
    public boolean alternativeSwingAnimation = false;
    @Config(description = "Render armor attributes with green text instead of blue one, just like with tools and weapons.")
    public boolean specialArmorAttributes = true;
    @Config(description = "Show a shield indicator similar to the attack indicator when actively blocking.")
    public boolean shieldIndicator = true;
    @Config(name = "hidden_offhand_items", description = "Items that will not be rendered as being held in first person when placed in the offhand.")
    List<String> hiddenOffhandItemsRaw = ConfigDataSet.toString(Registry.ITEM_REGISTRY, Items.TOTEM_OF_UNDYING);

    public ConfigDataSet<Item> hiddenOffhandItems;

    @Override
    public void afterConfigReload() {
        this.hiddenOffhandItems = ConfigDataSet.from(Registry.ITEM_REGISTRY, this.hiddenOffhandItemsRaw);
    }
}
