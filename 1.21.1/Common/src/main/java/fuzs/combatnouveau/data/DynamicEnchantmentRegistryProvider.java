package fuzs.combatnouveau.data;

import fuzs.puzzleslib.api.data.v2.AbstractRegistriesDatapackGenerator;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;

public class DynamicEnchantmentRegistryProvider extends AbstractRegistriesDatapackGenerator.Enchantments {

    public DynamicEnchantmentRegistryProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    protected void addBootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        this.add(net.minecraft.world.item.enchantment.Enchantments.SWEEPING_EDGE, Enchantment.enchantment(
                        Enchantment.definition(items.getOrThrow(ItemTags.SWORD_ENCHANTABLE), 2, 3,
                                Enchantment.dynamicCost(5, 9), Enchantment.dynamicCost(20, 9), 4, EquipmentSlotGroup.MAINHAND
                        ))
                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                        ResourceLocation.withDefaultNamespace("enchantment.sweeping_edge"),
                        Attributes.SWEEPING_DAMAGE_RATIO, new LevelBasedValue.Fraction(LevelBasedValue.perLevel(0.5F),
                        LevelBasedValue.perLevel(2.0F, 1.0F)
                ), AttributeModifier.Operation.ADD_VALUE
                )));
    }
}
