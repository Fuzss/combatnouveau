package fuzs.combatnouveau.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import net.minecraft.world.item.ItemUseAnimation;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class ServerConfig implements ConfigCore {
    static final String SWEEPING_CATEGORY = "sweeping";
    static final String COOLDOWN_CATEGORY = "cooldown";
    static final String ITEMS_CATEGORY = "items";
    static final String SHIELD_CATEGORY = "shield";

    @Config(description = "Health regenerates every 2 seconds, which requires more than 6 food points. Also food points will be directly consumed when healing. Surplus saturation does not yield quick health regeneration.")
    public boolean balancedFoodMechanics = false;
    @Config(description = "Player is knocked back by attacks which do not cause any damage, such as when hit by snowballs, eggs, and fishing rod hooks.")
    public boolean weakAttackKnockBack = true;
    @Config(description = "Sprinting and attacking no longer interfere with each other, making critical hits possible at all times.")
    public boolean criticalHitsWhileSprinting = true;
    @Config(description = "Expand all entity hitboxes by 10%, making hitting a target possible from a slightly greater range and with much increased accuracy.")
    public boolean inflateHitboxes = true;
    @Config(description = "Makes knockback stronger towards targets not on the ground (does not apply when in water).")
    public boolean upwardsKnockback = true;
    @Config(category = SWEEPING_CATEGORY,
            description = "Is the sweeping edge enchantment required to perform a sweep attack.")
    public boolean requireSweepingEdge = true;
    @Config(category = SWEEPING_CATEGORY, description = "Do not perform sweep attacks when sneaking.")
    public boolean noSweepingWhenSneaking = false;
    @Config(category = SWEEPING_CATEGORY, description = {
            "Allow sweep attack without hitting mobs, just by attacking air basically.",
            "This attack will not work when the attack button is held for continuous attacking."
    })
    public boolean airSweepAttack = true;
    @Config(description = "Attacking will no longer stop the player from sprinting. Very useful when swimming, so you can fight underwater without being stopped on every hit.")
    public boolean sprintAttacks = true;
    @Config(description = {
            "Force all entity hitboxes to have a cubic size of at least the defined value, making them easier to hit and shoot at.",
            "This only affects targeting an entity, no collisions or whatsoever. Useful for hitting e.g. bats, rabbits, silverfish, fish, and most baby animals.",
            "Set to zero to disable."
    })
    @Config.DoubleRange(min = 0.0, max = 16.0)
    public double minHitboxSize = 0.9;
    @Config(category = COOLDOWN_CATEGORY,
            description = "Holding down the attack button keeps attacking continuously. No more spam clicking required.")
    public boolean holdAttackButton = false;
    @Config(category = COOLDOWN_CATEGORY, description = {
            "Delay in ticks between attacks when holding the attack button is enabled.",
            "This basically also puts a cap on the max spam clicking speed."
    })
    @Config.IntRange(min = 0)
    public int holdAttackButtonDelay = 5;
    @Config(category = ITEMS_CATEGORY,
            description = "Eating and drinking may both be interrupted if the player is damaged.")
    public ConsumeInterruption consumeInterruption = ConsumeInterruption.BOTH;
    @Config(category = COOLDOWN_CATEGORY, description = "Attack cooldown is unaffected by switching hotbar items.")
    public boolean fastSwitching = true;
    @Config(category = COOLDOWN_CATEGORY,
            description = "Melee attacks that don't hit a target won't trigger the attack cooldown.")
    public boolean retainEnergyOnMiss = false;
    @Config(category = COOLDOWN_CATEGORY, description = {
            "Disable attacking when attack cooldown is below a certain percentage.",
            "Setting this to 0.0 means attacking is possible with any strength as in vanilla."
    })
    @Config.DoubleRange(min = 0.0, max = 1.0)
    public double minAttackStrength = 1.0;
    @Config(description = "Disables damage immunity when hit by a projectile. Makes it possible for entities to be hit by multiple projectiles at once (useful for the multishot enchantment).")
    public boolean noProjectileImmunity = true;
    @Config(category = SHIELD_CATEGORY, description = {
            "Shields knock back attackers (see MC-147694).",
            "NONE: Vanilla behavior, no knockback is dealt to attackers.",
            "CONSTANT: Always the same knockback is dealth to attackers.",
            "VARIABLE: The knockback strength is greater the more precisely the shield block is timed in regards to the attack."
    })
    public ShieldKnockback shieldKnockback = ShieldKnockback.VARIABLE;
    @Config(category = SHIELD_CATEGORY,
            description = "Amount of ticks after starting to block in which an attacker will be knocked back further than usual when \"shield_knockback\" is set to VARIABLE.")
    @Config.IntRange(min = 0)
    public int variableShieldKnockbackDelay = 20;

    public enum ShieldKnockback {
        NONE,
        CONSTANT,
        VARIABLE
    }

    public enum ConsumeInterruption {
        NONE(),
        EATING(ItemUseAnimation.EAT),
        DRINKING(ItemUseAnimation.DRINK),
        BOTH(ItemUseAnimation.EAT, ItemUseAnimation.DRINK);

        public final Set<ItemUseAnimation> itemUseAnimations;

        ConsumeInterruption(ItemUseAnimation... itemUseAnimations) {
            if (itemUseAnimations.length == 0) {
                this.itemUseAnimations = EnumSet.noneOf(ItemUseAnimation.class);
            } else {
                this.itemUseAnimations = EnumSet.copyOf(Arrays.asList(itemUseAnimations));
            }
        }
    }
}
