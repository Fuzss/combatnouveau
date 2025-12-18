package fuzs.combatnouveau.init;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.puzzleslib.api.data.v2.AbstractDatapackRegistriesProvider;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;

public class ModRegistry {
    public static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder().add(Registries.ENCHANTMENT,
            ModRegistry::bootstrapEnchantments);
    static final RegistryManager REGISTRIES = RegistryManager.from(CombatNouveau.MOD_ID);

    public static void bootstrap() {
        // NO-OP
    }

    /**
     * @see Enchantments#bootstrap(BootstrapContext)
     */
    public static void bootstrapEnchantments(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> itemLookup = context.lookup(Registries.ITEM);
        AbstractDatapackRegistriesProvider.registerEnchantment(context,
                Enchantments.SWEEPING_EDGE,
                Enchantment.enchantment(Enchantment.definition(itemLookup.getOrThrow(ItemTags.SWEEPING_ENCHANTABLE),
                                2,
                                3,
                                Enchantment.dynamicCost(5, 9),
                                Enchantment.dynamicCost(20, 9),
                                4,
                                EquipmentSlotGroup.MAINHAND))
                        .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(Identifier.withDefaultNamespace(
                                        "enchantment.sweeping_edge"),
                                        Attributes.SWEEPING_DAMAGE_RATIO,
                                        new LevelBasedValue.Fraction(LevelBasedValue.perLevel(0.5F),
                                                LevelBasedValue.perLevel(2.0F, 1.0F)),
                                        AttributeModifier.Operation.ADD_VALUE)));
    }
}
