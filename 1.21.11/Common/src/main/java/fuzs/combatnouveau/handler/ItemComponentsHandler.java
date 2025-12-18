package fuzs.combatnouveau.handler;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.CommonConfig;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.*;
import net.minecraft.world.item.equipment.ArmorType;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class ItemComponentsHandler {
    public static final Identifier BASE_ENTITY_INTERACTION_RANGE_ID = Identifier.withDefaultNamespace(
            "base_entity_interaction_range");
    public static final Set<Identifier> BASE_ATTRIBUTE_MODIFIER_IDS = Stream.concat(Stream.of(
                    BASE_ENTITY_INTERACTION_RANGE_ID),
            Arrays.stream(ArmorType.values())
                    .map(ArmorType::getName)
                    .map((String nameValue) -> "armor." + nameValue)
                    .map(Identifier::withDefaultNamespace)).collect(ImmutableSet.toImmutableSet());

    public static void onFinalizeItemComponents(Item item, Consumer<Function<DataComponentMap, DataComponentPatch>> consumer) {
        if (!CombatNouveau.CONFIG.getHolder(CommonConfig.class).isAvailable()) {
            return;
        }

        if (CombatNouveau.CONFIG.get(CommonConfig.class).increaseStackSize) {
            consumer.accept((DataComponentMap dataComponents) -> {
                return getMaxStackSizePatch(item);
            });
        }

        consumer.accept((DataComponentMap dataComponents) -> {
            return getUseCooldownPatch(item);
        });

        if (CombatNouveau.CONFIG.get(CommonConfig.class).noItemDurabilityPenalty) {
            consumer.accept((DataComponentMap dataComponents) -> {
                DataComponentPatch weaponPatch = getWeaponPatch(dataComponents);
                if (weaponPatch != null) {
                    return weaponPatch;
                } else {
                    DataComponentPatch toolPatch = getToolPatch(dataComponents);
                    return toolPatch != null ? toolPatch : DataComponentPatch.EMPTY;
                }
            });
        }

        if (CombatNouveau.CONFIG.get(CommonConfig.class).fastDrinking) {
            consumer.accept(ItemComponentsHandler::getConsumablePatch);
        }

        if (item == Items.SHIELD) {
            consumer.accept(ItemComponentsHandler::getBlocksAttacksPatch);
        }

        consumer.accept((DataComponentMap dataComponents) -> {
            return getAttributeModifiersPatch(item, dataComponents);
        });
    }

    private static DataComponentPatch getMaxStackSizePatch(Item item) {
        if (item == Items.SNOWBALL || isEgg(item)) {
            return DataComponentPatch.builder().set(DataComponents.MAX_STACK_SIZE, 64).build();
        } else if (isPotion(item)) {
            return DataComponentPatch.builder().set(DataComponents.MAX_STACK_SIZE, 16).build();
        } else {
            return DataComponentPatch.EMPTY;
        }
    }

    private static DataComponentPatch getUseCooldownPatch(Item item) {
        if (item == Items.SNOWBALL || isEgg(item)) {
            if (CombatNouveau.CONFIG.get(CommonConfig.class).throwablesDelay) {
                return DataComponentPatch.builder()
                        .set(DataComponents.USE_COOLDOWN,
                                new UseCooldown(0.2F, Optional.of(CombatNouveau.id("throwables"))))
                        .build();
            }
        } else if (isThrowablePotion(item)) {
            if (CombatNouveau.CONFIG.get(CommonConfig.class).increaseStackSize) {
                return DataComponentPatch.builder()
                        .set(DataComponents.USE_COOLDOWN,
                                new UseCooldown(0.35F, Optional.of(CombatNouveau.id("potions"))))
                        .build();
            }
        }

        return DataComponentPatch.EMPTY;
    }

    private static boolean isEgg(Item item) {
        return item == Items.EGG || item == Items.BROWN_EGG || item == Items.BLUE_EGG;
    }

    private static boolean isPotion(Item item) {
        return item == Items.POTION || isThrowablePotion(item);
    }

    private static boolean isThrowablePotion(Item item) {
        return item == Items.SPLASH_POTION || item == Items.LINGERING_POTION;
    }

    private static @Nullable DataComponentPatch getWeaponPatch(DataComponentMap dataComponents) {
        Weapon weapon = dataComponents.get(DataComponents.WEAPON);
        if (weapon != null && weapon.itemDamagePerAttack() == 2) {
            return DataComponentPatch.builder()
                    .set(DataComponents.WEAPON, new Weapon(1, weapon.disableBlockingForSeconds()))
                    .build();
        } else {
            return null;
        }
    }

    private static @Nullable DataComponentPatch getToolPatch(DataComponentMap dataComponents) {
        Tool tool = dataComponents.get(DataComponents.TOOL);
        if (tool != null && tool.damagePerBlock() == 2) {
            return DataComponentPatch.builder()
                    .set(DataComponents.TOOL,
                            new Tool(tool.rules(), tool.defaultMiningSpeed(), 1, tool.canDestroyBlocksInCreative()))
                    .build();
        } else {
            return null;
        }
    }

    private static DataComponentPatch getConsumablePatch(DataComponentMap dataComponents) {
        Consumable consumable = dataComponents.get(DataComponents.CONSUMABLE);
        if (consumable != null && consumable.animation() == ItemUseAnimation.DRINK) {
            return DataComponentPatch.builder()
                    .set(DataComponents.CONSUMABLE,
                            new Consumable(1.0F,
                                    consumable.animation(),
                                    consumable.sound(),
                                    consumable.hasConsumeParticles(),
                                    consumable.onConsumeEffects()))
                    .build();
        } else {
            return DataComponentPatch.EMPTY;
        }
    }

    private static DataComponentPatch getBlocksAttacksPatch(DataComponentMap dataComponents) {
        BlocksAttacks blocksAttacks = dataComponents.get(DataComponents.BLOCKS_ATTACKS);
        if (blocksAttacks != null) {
            return DataComponentPatch.builder()
                    .set(DataComponents.BLOCKS_ATTACKS,
                            new BlocksAttacks(CombatNouveau.CONFIG.get(CommonConfig.class).removeShieldDelay ? 0.0F :
                                    blocksAttacks.blockDelaySeconds(),
                                    blocksAttacks.disableCooldownScale(),
                                    ImmutableList.copyOf(Lists.transform(blocksAttacks.damageReductions(),
                                            (BlocksAttacks.DamageReduction damageReduction) -> {
                                                return new BlocksAttacks.DamageReduction((float) CombatNouveau.CONFIG.get(
                                                        CommonConfig.class).horizontalBlockingAngle,
                                                        damageReduction.type(),
                                                        damageReduction.base(),
                                                        damageReduction.factor());
                                            })),
                                    blocksAttacks.itemDamage(),
                                    blocksAttacks.bypassedBy(),
                                    blocksAttacks.blockSound(),
                                    blocksAttacks.disableSound()))
                    .build();
        } else {
            return DataComponentPatch.EMPTY;
        }
    }

    private static DataComponentPatch getAttributeModifiersPatch(Item item, DataComponentMap dataComponents) {
        List<ItemAttributeModifiers.Entry> itemAttributeModifiers = dataComponents.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS,
                ItemAttributeModifiers.EMPTY).modifiers();
        itemAttributeModifiers = setAttributeValue(item,
                itemAttributeModifiers,
                Attributes.ATTACK_DAMAGE,
                Item.BASE_ATTACK_DAMAGE_ID,
                CombatNouveau.CONFIG.get(CommonConfig.class).attackDamageOverrides);
        itemAttributeModifiers = setAttributeValue(item,
                itemAttributeModifiers,
                Attributes.ATTACK_SPEED,
                Item.BASE_ATTACK_SPEED_ID,
                CombatNouveau.CONFIG.get(CommonConfig.class).attackSpeedOverrides);
        List<ItemAttributeModifiers.Entry> itemAttributeModifiers2 = setAttributeValue(item,
                itemAttributeModifiers,
                Attributes.ENTITY_INTERACTION_RANGE,
                BASE_ENTITY_INTERACTION_RANGE_ID,
                CombatNouveau.CONFIG.get(CommonConfig.class).entityInteractionRangeOverrides);
        if (itemAttributeModifiers == itemAttributeModifiers2) {
            if (CombatNouveau.CONFIG.get(CommonConfig.class).additionalEntityInteractionRange) {
                OptionalDouble attackRangeBonus = getAttackRangeBonus(item, dataComponents);
                if (attackRangeBonus.isPresent()) {
                    itemAttributeModifiers = setAttributeValue(itemAttributeModifiers,
                            Attributes.ENTITY_INTERACTION_RANGE,
                            BASE_ENTITY_INTERACTION_RANGE_ID,
                            attackRangeBonus.getAsDouble());
                }
            }
        } else {
            itemAttributeModifiers = itemAttributeModifiers2;
        }
        if (dataComponents.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers()
                != itemAttributeModifiers) {
            return DataComponentPatch.builder()
                    .set(DataComponents.ATTRIBUTE_MODIFIERS,
                            new ItemAttributeModifiers(ImmutableList.copyOf(itemAttributeModifiers)))
                    .build();
        } else {
            return DataComponentPatch.EMPTY;
        }
    }

    private static List<ItemAttributeModifiers.Entry> setAttributeValue(Item item, List<ItemAttributeModifiers.Entry> itemAttributeModifiers, Holder<Attribute> attribute, Identifier id, ConfigDataSet<Item> attackDamageOverrides) {
        if (attackDamageOverrides.contains(item)) {
            double newValue = attackDamageOverrides.<Double>getOptional(item, 0).orElseThrow();
            return setAttributeValue(itemAttributeModifiers, attribute, id, newValue);
        } else {
            return itemAttributeModifiers;
        }
    }

    private static List<ItemAttributeModifiers.Entry> setAttributeValue(List<ItemAttributeModifiers.Entry> itemAttributeModifiers, Holder<Attribute> attribute, Identifier id, double newValue) {
        itemAttributeModifiers = new ArrayList<>(itemAttributeModifiers);
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
                return itemAttributeModifiers;
            }
        }
        itemAttributeModifiers.add(newEntry);
        return itemAttributeModifiers;
    }

    private static OptionalDouble getAttackRangeBonus(Item item, DataComponentMap dataComponents) {
        if (item == Items.TRIDENT || ToolComponentsHelper.hasComponentsForBlocks(dataComponents,
                BlockTags.MINEABLE_WITH_HOE)) {
            return OptionalDouble.of(1.0);
        } else if (ToolComponentsHelper.isWeapon(dataComponents)) {
            return OptionalDouble.of(0.5);
        } else if (ToolComponentsHelper.isTool(dataComponents)) {
            return OptionalDouble.of(0.0);
        } else {
            return OptionalDouble.empty();
        }
    }
}
