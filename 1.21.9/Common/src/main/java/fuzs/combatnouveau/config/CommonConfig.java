package fuzs.combatnouveau.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import fuzs.puzzleslib.api.config.v3.serialization.KeyedValueProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.List;

public class CommonConfig implements ConfigCore {
    static final String ITEM_ATTRIBUTES_CATEGORY = "item_attributes";
    static final String ENTITY_ATTRIBUTES_CATEGORY = "entity_attributes";

    @Config(category = ServerConfig.ITEMS_CATEGORY,
            description = "Increase snowball and egg stack size from 16 to 64, and potion stack size from 1 to 16 (only for potions of the same type of course).",
            gameRestart = true)
    public boolean increaseStackSize = true;
    @Config(category = ServerConfig.ITEMS_CATEGORY,
            description = "Only damages axes by 1 durability instead of 2 when attacking so they properly be used as weapons.",
            gameRestart = true)
    public boolean noAxeAttackPenalty = true;
    @Config(category = ServerConfig.SWEEPING_CATEGORY,
            description = "Only apply half the sweeping damage to indirectly hit mobs for better balancing of the sweeping feature.",
            gameRestart = true)
    public boolean halveSweepingDamage = true;
    @Config(category = ServerConfig.SHIELD_CATEGORY,
            description = "Skip 5 tick warm-up delay when activating a shield, so they become effective instantly.",
            gameRestart = true)
    public boolean removeShieldDelay = true;
    @Config(category = ServerConfig.ITEMS_CATEGORY,
            description = "It only takes 20 ticks to drink liquid foods (such as potions, milk, and bottled liquids) instead of 32 or 40.",
            gameRestart = true)
    public boolean fastDrinking = true;
    @Config(category = ServerConfig.SHIELD_CATEGORY, description = {
            "Arc of available protection depending on what angle the attack is coming from and where the player is looking (means the lower this angle the closer you need to be facing your attacker).",
            "Vanilla protection arc is 90 degrees, which has been reduced to around 50 in combat tests.",
            "This does not change the protection arc for projectiles which remains at 90 degress."
    }, gameRestart = true)
    @Config.DoubleRange(min = 0.0, max = 180.0)
    public double horizontalBlockingAngle = 50.0;
    @Config(category = ServerConfig.ITEMS_CATEGORY,
            description = "Add a delay of 4 ticks between throwing snowballs or eggs, just like with ender pearls.",
            gameRestart = true)
    public boolean throwablesDelay = true;
    @Config(category = ITEM_ATTRIBUTES_CATEGORY, name = "attack_damage_overrides", description = {
            "Overrides for setting and balancing attack damage values of items.",
            "As with all items, this value is added on top of the default attack strength of the player (which is 1.0 by default).",
            "Format for every entry is \"<namespace>:<path>,<amount>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color."
    }, gameRestart = true)
    List<String> attackDamageOverridesRaw = KeyedValueProvider.toString(Registries.ITEM);
    @Config(category = ITEM_ATTRIBUTES_CATEGORY, name = "attack_speed_overrides", description = {
            "Overrides for setting and balancing attack speed values of items.",
            "As with all items, this value is added on top of the default attack speed of the player (which is 4.0 by default).",
            "Format for every entry is \"<namespace>:<path>,<amount>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color."
    }, gameRestart = true)
    List<String> attackSpeedOverridesRaw = KeyedValueProvider.toString(Registries.ITEM);
    @Config(category = ITEM_ATTRIBUTES_CATEGORY, name = "entity_interaction_range_overrides", description = {
            "Overrides for setting and balancing attack reach values of items.",
            "Takes precedence over any changes made by \"additional_attack_reach\" option.",
            "As with all items, this value is added on top of the default attack reach of the player (which is 3.0 by default, and has a hard cap at 6.0).",
            "Format for every entry is \"<namespace>:<path>,<amount>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color."
    }, gameRestart = true)
    List<String> entityInteractionRangeOverridesRaw = KeyedValueProvider.toString(Registries.ITEM);
    @Config(category = ITEM_ATTRIBUTES_CATEGORY,
            description = "Makes it so that swords, hoes, and tridents have an increased reach when attacking.",
            gameRestart = true)
    public boolean additionalEntityInteractionRange = true;
    @Config(category = ENTITY_ATTRIBUTES_CATEGORY,
            description = "Double the base player attack strength to 2.0.",
            gameRestart = true)
    public boolean doublePlayerAttackStrength = false;

    public ConfigDataSet<Item> attackDamageOverrides;
    public ConfigDataSet<Item> attackSpeedOverrides;
    public ConfigDataSet<Item> entityInteractionRangeOverrides;

    @Override
    public void afterConfigReload() {
        this.attackDamageOverrides = ConfigDataSet.from(Registries.ITEM, this.attackDamageOverridesRaw, double.class);
        this.attackSpeedOverrides = ConfigDataSet.from(Registries.ITEM, this.attackSpeedOverridesRaw, double.class);
        this.entityInteractionRangeOverrides = ConfigDataSet.from(Registries.ITEM,
                this.entityInteractionRangeOverridesRaw,
                double.class);
    }
}
