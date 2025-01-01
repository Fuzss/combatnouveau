package fuzs.combatnouveau.handler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.CommonConfig;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class AttackAttributeHandler {
    public static final ResourceLocation BASE_ENTITY_INTERACTION_RANGE_ID = ResourceLocation.withDefaultNamespace(
            "base_entity_interaction_range");
    public static final Set<ResourceLocation> BASE_ATTRIBUTE_MODIFIER_IDS;
    public static final Map<Class<? extends Item>, Double> ATTACK_RANGE_BONUS_OVERRIDES = ImmutableMap.of(TridentItem.class,
            1.0,
            HoeItem.class,
            1.0,
            MaceItem.class,
            0.5,
            SwordItem.class,
            0.5,
            DiggerItem.class,
            0.0);

    static {
        BASE_ATTRIBUTE_MODIFIER_IDS = Stream.concat(Stream.of(BASE_ENTITY_INTERACTION_RANGE_ID),
                Arrays.stream(ArmorType.values())
                        .map(ArmorType::getName)
                        .map(s -> "armor." + s)
                        .map(ResourceLocationHelper::withDefaultNamespace)).collect(ImmutableSet.toImmutableSet());
    }

    public static void onComputeItemAttributeModifiers(Item item, List<ItemAttributeModifiers.Entry> itemAttributeModifiers) {
        if (!CombatNouveau.CONFIG.getHolder(CommonConfig.class).isAvailable()) return;
        setAttributeValue(item,
                itemAttributeModifiers,
                Attributes.ATTACK_DAMAGE,
                Item.BASE_ATTACK_DAMAGE_ID,
                CombatNouveau.CONFIG.get(CommonConfig.class).attackDamageOverrides);
        setAttributeValue(item,
                itemAttributeModifiers,
                Attributes.ATTACK_SPEED,
                Item.BASE_ATTACK_SPEED_ID,
                CombatNouveau.CONFIG.get(CommonConfig.class).attackSpeedOverrides);
        if (!setAttributeValue(item,
                itemAttributeModifiers,
                Attributes.ENTITY_INTERACTION_RANGE,
                BASE_ENTITY_INTERACTION_RANGE_ID,
                CombatNouveau.CONFIG.get(CommonConfig.class).entityInteractionRangeOverrides)) {
            if (CombatNouveau.CONFIG.get(CommonConfig.class).additionalEntityInteractionRange) {
                for (Map.Entry<Class<? extends Item>, Double> entry : ATTACK_RANGE_BONUS_OVERRIDES.entrySet()) {
                    if (entry.getKey().isInstance(item)) {
                        setAttributeValue(itemAttributeModifiers,
                                Attributes.ENTITY_INTERACTION_RANGE,
                                BASE_ENTITY_INTERACTION_RANGE_ID,
                                entry.getValue());
                        break;
                    }
                }
            }
        }
    }

    private static boolean setAttributeValue(Item item, List<ItemAttributeModifiers.Entry> itemAttributeModifiers, Holder<Attribute> attribute, ResourceLocation id, ConfigDataSet<Item> attackDamageOverrides) {
        if (attackDamageOverrides.contains(item)) {
            double newValue = attackDamageOverrides.<Double>getOptional(item, 0).orElseThrow();
            setAttributeValue(itemAttributeModifiers, attribute, id, newValue);
            return true;
        } else {
            return false;
        }
    }

    private static void setAttributeValue(List<ItemAttributeModifiers.Entry> itemAttributeModifiers, Holder<Attribute> attribute, ResourceLocation id, double newValue) {
        AttributeModifier attributeModifier = new AttributeModifier(id,
                newValue,
                AttributeModifier.Operation.ADD_VALUE);
        ItemAttributeModifiers.Entry newEntry = new ItemAttributeModifiers.Entry(attribute,
                attributeModifier,
                EquipmentSlotGroup.MAINHAND);
        ListIterator<ItemAttributeModifiers.Entry> iterator = itemAttributeModifiers.listIterator();
        while (iterator.hasNext()) {
            ItemAttributeModifiers.Entry entry = iterator.next();
            if (entry.slot() == EquipmentSlotGroup.MAINHAND && entry.matches(attribute, id)) {
                iterator.set(newEntry);
                return;
            }
        }
        itemAttributeModifiers.add(newEntry);
    }

    public static void onFinalizeItemComponents(Item item, Consumer<Function<DataComponentMap, DataComponentPatch>> consumer) {
        if (!CombatNouveau.CONFIG.getHolder(CommonConfig.class).isAvailable()) return;
        if (!CombatNouveau.CONFIG.get(CommonConfig.class).increaseStackSize) return;
        if (item == Items.SNOWBALL || item == Items.EGG) {
            consumer.accept((DataComponentMap dataComponents) -> {
                return DataComponentPatch.builder().set(DataComponents.MAX_STACK_SIZE, 64).build();
            });
        } else if (item == Items.POTION) {
            consumer.accept((DataComponentMap dataComponents) -> {
                return DataComponentPatch.builder().set(DataComponents.MAX_STACK_SIZE, 16).build();
            });
        }
    }
}
