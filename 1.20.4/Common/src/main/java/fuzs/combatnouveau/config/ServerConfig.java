package fuzs.combatnouveau.config;

import fuzs.combatnouveau.handler.CombatTestHandler;
import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.List;

public class ServerConfig implements ConfigCore {
    @Config(description = "Health regenerates every 2 seconds, which requires more than 6 food points. Also food points will be directly consumed when healing. Surplus saturation does not yield quick health regeneration.")
    public boolean balancedFoodMechanics = false;
    @Config(description = "Player is knocked back by attacks which do not cause any damage, such as when hit by snowballs, eggs, and fishing rod hooks.")
    public boolean weakAttacksKnockBackPlayers = true;
    @Config(description = "Sprinting and attacking no longer interfere with each other, making critical hits possible at all times.")
    public boolean criticalHitsWhileSprinting = true;
    @Config(description = "Expand all entity hitboxes by 10%, making hitting a target possible from a slightly greater range and with much increased accuracy.")
    public boolean inflateHitboxes = true;
    @Config(description = "Makes knockback stronger towards targets not on the ground (does not apply when in water).")
    public boolean upwardsKnockback = true;
    @Config(category = "attributes", name = "attack_damage_overrides", description = {"Overrides for setting and balancing attack damage values of items.", "As with all items, this value is added on top of the default attack strength of the player (which is 1.0 by default).", "Format for every entry is \"<namespace>:<path>,<amount>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color."})
    List<String> attackDamageOverridesRaw = ConfigDataSet.toString(Registries.ITEM);
    @Config(category = "attributes", name = "attack_speed_overrides", description = {"Overrides for setting and balancing attack speed values of items.", "As with all items, this value is added on top of the default attack speed of the player (which is 4.0 by default).", "Format for every entry is \"<namespace>:<path>,<amount>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color."})
    List<String> attackSpeedOverridesRaw = ConfigDataSet.toString(Registries.ITEM);
    @Config(category = "attributes", name = "attack_reach_overrides", description = {"Overrides for setting and balancing attack reach values of items.", "Takes precedence over any changes made by \"additional_attack_reach\" option.", "As with all items, this value is added on top of the default attack reach of the player (which is 3.0 by default, and has a hard cap at 6.0).", "Format for every entry is \"<namespace>:<path>,<amount>\". Tags are supported, must be in the format of \"#<namespace>:<path>\". Namespace may be omitted to use \"minecraft\" by default. May use asterisk as wildcard parameter via pattern matching, e.g. \"minecraft:*_shulker_box\" to match all shulker boxes no matter of color."})
    List<String> attackReachOverridesRaw = ConfigDataSet.toString(Registries.ITEM);
    @Config(category = "attributes", description = "Makes it so that swords, hoes, and tridents have an increased reach when attacking.")
    public boolean additionalAttackReach = true;
    @Config(category = "sweeping", description = "Is the sweeping edge enchantment required to perform a sweep attack.")
    public boolean requireSweepingEdge = true;
    @Config(category = "sweeping", description = "Only apply half the sweeping damage to indirectly hit mobs for better balancing of the sweeping feature.")
    public boolean halfSweepingDamage = true;
    @Config(category = "sweeping", description = "Do not perform sweep attacks when sneaking.")
    public boolean noSweepingWhenSneaking = false;
    @Config(category = "sweeping", description = {"Allow sweep attack without hitting mobs, just by attacking air basically.", "This attack will not work when the attack button is held for continuous attacking."})
    public boolean airSweepAttack = true;
    @Config(description = "Attacking will no longer stop the player from sprinting. Very useful when swimming, so you can fight underwater without being stopped on every hit.")
    public boolean sprintAttacks = true;
    @Config(description = {"Force all entity hitboxes to have a cubic size of at least 0.9 blocks, making them easier to hit and shoot.", "This only affects targeting an entity, no collisions or whatsoever. Useful for hitting e.g. bats, rabbits, silverfish, fish, and most baby animals."})
    public boolean minHitboxSize = true;
    @Config(category = "cooldown", description = "Holding down the attack button keeps attacking continuously. No more spam clicking required.")
    public boolean holdAttackButton = false;
    @Config(category = "cooldown", description = {"Delay in ticks between attacks when holding the attack button is enabled.", "This basically also puts a cap on the max spam clicking speed."})
    @Config.IntRange(min = 0)
    public int holdAttackButtonDelay = 5;
    @Config(category = "items", description = "Increase snowball and egg stack size from 16 to 64, and potion stack size from 1 to 16 (only for potions of the same type of course).")
    public boolean increaseStackSize = true;
    @Config(category = "items", description = "Add a delay of 4 ticks between throwing snowballs or eggs, just like with ender pearls.")
    public boolean throwablesDelay = true;
    @Config(category = "items", description = "Eating and drinking both are interrupted if the player is damaged.")
    public boolean eatingInterruption = true;
    @Config(category = "items", description = "It only takes 20 ticks to drink liquid foods (such as potions, milk, and bottled liquids) instead of 32 or 40.")
    public boolean fastDrinking = true;
    @Config(category = "items", description = "Only damages axes by 1 durability instead of 2 when attacking so they properly be used as weapons.")
    public boolean noAxeAttackPenalty = true;
    @Config(category = "cooldown", description = "Attack cooldown is unaffected by switching hotbar items.")
    public boolean fastSwitching = true;
    @Config(category = "cooldown", description = "Melee attacks that don't hit a target won't trigger the attack cooldown.")
    public boolean retainEnergyOnMiss = false;
    @Config(category = "cooldown", description = {"Disable attacking when attack cooldown is below a certain percentage.", "Setting this to 0.0 means attacking is possible with any strength as in vanilla."})
    @Config.DoubleRange(min = 0.0, max = 1.0)
    public double minAttackStrength = 1.0;
    @Config(description = "Disables damage immunity when hit by a projectile. Makes it possible for entities to be hit by multiple projectiles at once (useful for the multishot enchantment).")
    public boolean noProjectileImmunity = true;
    @Config(category = "shield", description = "Skip 5 tick warm-up delay when activating a shield, so they become effective instantly.")
    public boolean removeShieldDelay = true;
    @Config(category = "shield", description = {"Shields knock back attackers (see MC-147694).", "NONE: Vanilla behavior, no knockback is dealt to attackers.", "CONSTANT: Always the same knockback is dealth to attackers.", "VARIABLE: The knockback strength is greater the more precisely the shield block is timed in regards to the attack."})
    public ShieldKnockback shieldKnockback = ShieldKnockback.VARIABLE;
    @Config(category = "shield", description = "Amount of ticks after starting to block in which an attacker will be knocked back further than usual when \"shield_knockback\" is set to VARIABLE.")
    @Config.IntRange(min = 0)
    public int variableShieldKnockbackDelay = 20;
    @Config(category = "shield", description = {"Arc of available protection depending on what angle the attack is coming from and where the player is looking (means the lower this angle the closer you need to be facing your attacker).", "Vanilla protection arc is 180 degrees, which has been reduced to around 100 in combat tests.", "This does not change the protection arc for projectiles which remains at 180 degress."})
    @Config.DoubleRange(min = 0.0, max = 360.0)
    public double shieldProtectionArc = 100.0;

    public ConfigDataSet<Item> attackDamageOverrides;
    public ConfigDataSet<Item> attackSpeedOverrides;
    public ConfigDataSet<Item> attackReachOverrides;

    @Override
    public void afterConfigReload() {
        this.attackDamageOverrides = ConfigDataSet.from(Registries.ITEM, this.attackDamageOverridesRaw, (i, o) -> i != 1 || ((double) o) >= 0.0, double.class);
        this.attackSpeedOverrides = ConfigDataSet.from(Registries.ITEM, this.attackSpeedOverridesRaw, (i, o) -> i != 1 || ((double) o) >= 0.0, double.class);
        this.attackReachOverrides = ConfigDataSet.from(Registries.ITEM, this.attackReachOverridesRaw, (i, o) -> i != 1 || ((double) o) >= 0.0, double.class);
        CombatTestHandler.setMaxStackSize(this);
    }

    public enum ShieldKnockback {
        NONE, CONSTANT, VARIABLE
    }
}
