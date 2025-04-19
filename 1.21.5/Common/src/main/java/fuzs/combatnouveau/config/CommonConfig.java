package fuzs.combatnouveau.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import fuzs.puzzleslib.api.config.v3.serialization.KeyedValueProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.List;

public class CommonConfig implements ConfigCore {
    static final String ATTRIBUTES_CATEGORY = "attributes";

    @Config(description = "Increase snowball and egg stack size from 16 to 64, and potion stack size from 1 to 16 (only for potions of the same type of course).", gameRestart = true)
    public boolean increaseStackSize = true;
    @Config(description = "Only apply half the sweeping damage to indirectly hit mobs for better balancing of the sweeping feature.", gameRestart = true)
    public boolean halveSweepingDamage = true;
    @Config(category = ATTRIBUTES_CATEGORY, name = "attack_damage_overrides", description = {"Overrides for setting and balancing attack damage values of items.", "As with all items, this value is added on top of the default attack strength of the player (which is 1.0 by default).", "Format for every entry is \"<namespace>:<path>,<amount>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color."}, gameRestart = true)
    List<String> attackDamageOverridesRaw = KeyedValueProvider.toString(Registries.ITEM);
    @Config(category = ATTRIBUTES_CATEGORY, name = "attack_speed_overrides", description = {"Overrides for setting and balancing attack speed values of items.", "As with all items, this value is added on top of the default attack speed of the player (which is 4.0 by default).", "Format for every entry is \"<namespace>:<path>,<amount>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color."}, gameRestart = true)
    List<String> attackSpeedOverridesRaw = KeyedValueProvider.toString(Registries.ITEM);
    @Config(category = ATTRIBUTES_CATEGORY, name = "entity_interaction_range_overrides", description = {"Overrides for setting and balancing attack reach values of items.", "Takes precedence over any changes made by \"additional_attack_reach\" option.", "As with all items, this value is added on top of the default attack reach of the player (which is 3.0 by default, and has a hard cap at 6.0).", "Format for every entry is \"<namespace>:<path>,<amount>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color."}, gameRestart = true)
    List<String> entityInteractionRangeOverridesRaw = KeyedValueProvider.toString(Registries.ITEM);
    @Config(category = ATTRIBUTES_CATEGORY, description = "Makes it so that swords, hoes, and tridents have an increased reach when attacking.", gameRestart = true)
    public boolean additionalEntityInteractionRange = true;

    public ConfigDataSet<Item> attackDamageOverrides;
    public ConfigDataSet<Item> attackSpeedOverrides;
    public ConfigDataSet<Item> entityInteractionRangeOverrides;

    @Override
    public void afterConfigReload() {
        this.attackDamageOverrides = ConfigDataSet.from(Registries.ITEM, this.attackDamageOverridesRaw, double.class);
        this.attackSpeedOverrides = ConfigDataSet.from(Registries.ITEM, this.attackSpeedOverridesRaw, double.class);
        this.entityInteractionRangeOverrides = ConfigDataSet.from(Registries.ITEM, this.entityInteractionRangeOverridesRaw, double.class);
    }
}
