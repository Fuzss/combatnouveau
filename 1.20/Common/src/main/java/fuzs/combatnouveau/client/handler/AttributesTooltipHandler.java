package fuzs.combatnouveau.client.handler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import fuzs.combatnouveau.core.CommonAbstractions;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;

public class AttributesTooltipHandler {
    protected static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    private static final Map<Attribute, ToDoubleBiFunction<Player, ItemStack>> MAP = ImmutableMap.of(Attributes.ATTACK_DAMAGE, (player, itemStack) -> {
        return EnchantmentHelper.getDamageBonus(itemStack, MobType.UNDEFINED);
    });

    public static void onItemTooltip(ItemStack stack, @Nullable Player player, List<Component> lines, TooltipFlag context) {


        int startIndex = removeAllAttributes(lines);
        if (startIndex != -1) {
            addOldStyleAttributes(lines, stack, player, startIndex);
        }


//
//
//        if (!GoldenAgeCombat.CONFIG.getHolder(ServerConfig.class).isAvailable()) return;
//        if (GoldenAgeCombat.CONFIG.get(ClientConfig.class).tooltip.removeAllAttributes || GoldenAgeCombat.CONFIG.get(ClientConfig.class).tooltip.oldAttributes) {
//            if (!GoldenAgeCombat.CONFIG.get(ClientConfig.class).tooltip.removeAllAttributes && startIndex != -1 && GoldenAgeCombat.CONFIG.get(ClientConfig.class).tooltip.oldAttributes) {
//                addOldStyleAttributes(lines, stack, player, startIndex);
//            }
//            return;
//        }
//        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).classic.removeAttackCooldown) {
//            removeAttribute(lines, Attributes.ATTACK_SPEED);
//        }
//        if (GoldenAgeCombat.CONFIG.get(ServerConfig.class).attributes.attackRange) {
//            replaceOrAddDefaultAttribute(lines, AttackAttributeHandler.BASE_ATTACK_RANGE_UUID, stack, player);
//        } else {
//            removeAttribute(lines, CommonAbstractions.INSTANCE.getAttackRangeAttribute());
//        }
//        if (GoldenAgeCombat.CONFIG.get(ClientConfig.class).tooltip.specialArmorAttributes && stack.getItem() instanceof ArmorItem item) {
//            replaceOrAddDefaultAttribute(lines, ARMOR_MODIFIER_UUID_PER_SLOT[item.getType().getSlot().getIndex()], stack, player);
//        }
    }

    private static void removeAttribute(List<Component> list, Attribute attribute) {
        list.removeIf(component -> matchesAttributeComponent(component, attribute, null));
    }

    private static boolean matchesAttributeComponent(Component component, Attribute attribute, @Nullable AttributeModifier attributeModifier) {
        TranslatableContents translatableContents = null;
        if (component.getContents() instanceof TranslatableContents translatableContents1) {
            translatableContents = translatableContents1;
        } else if (component instanceof MutableComponent mutableComponent && !mutableComponent.getSiblings().isEmpty() && mutableComponent.getSiblings().get(0).getContents() instanceof TranslatableContents translatableContents1) {
            translatableContents = translatableContents1;
        }
        if (translatableContents != null) {
            double scaledAmount = 0.0;
            String translationKey = null;
            if (attributeModifier != null) {
                double attributeAmount = attributeModifier.getAmount();
                scaledAmount = getScaledAttributeAmount(attribute, attributeModifier, attributeAmount);
                if (attributeAmount > 0.0D) {
                    translationKey = "attribute.modifier.plus." + attributeModifier.getOperation().toValue();
                } else if (attributeAmount < 0.0D) {
                    scaledAmount *= -1.0D;
                    translationKey = "attribute.modifier.take." + attributeModifier.getOperation().toValue();
                }
            }
            Object[] args = translatableContents.getArgs();
            if ((attributeModifier == null || translationKey != null && translatableContents.getKey().equals(translationKey)) && args.length >= 2) {
                if (attributeModifier == null || args[0].equals(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(scaledAmount))) {
                    if (args[1] instanceof Component component1 && component1.getContents() instanceof TranslatableContents translatableComponent1) {
                        return translatableComponent1.getKey().equals(attribute.getDescriptionId());
                    }
                }
            }
        }
        return false;
    }

    private static void replaceOrAddDefaultAttribute(List<Component> lines, UUID attributeId, ItemStack stack, @Nullable Player player) {
        Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> attributesBySlot = getAttributesBySlot(stack);
        for (Map.Entry<EquipmentSlot, Multimap<Attribute, AttributeModifier>> slotToAttributeMap : attributesBySlot.entrySet()) {
            List<Map.Entry<Attribute, AttributeModifier>> attributeModifier = Lists.newArrayList();
            for (Map.Entry<Attribute, AttributeModifier> attributeToModifier : slotToAttributeMap.getValue().entries()) {
                if (attributeToModifier.getValue().getId().equals(attributeId)) {
                    attributeModifier.add(attributeToModifier);
                }
            }
            for (Map.Entry<Attribute, AttributeModifier> entry : attributeModifier) {
                double attributeBaseAmount = entry.getValue().getAmount();
                double attributeAmount = attributeBaseAmount;
                if (player != null) {
                    attributeAmount += player.getAttributeBaseValue(entry.getKey());
                }
                if (attributeAmount != 0.0) {
                    double scaledAmount = getScaledAttributeAmount(entry.getKey(), entry.getValue(), attributeAmount);
                    for (int i = 0; i < lines.size(); i++) {
                        Component component = lines.get(i);
                        if (attributeBaseAmount != 0.0) {
                            if (matchesAttributeComponent(component, entry.getKey(), entry.getValue())) {
                                lines.set(i, Component.literal(" ").append(Component.translatable("attribute.modifier.equals." + entry.getValue().getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(scaledAmount), Component.translatable(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN));
                                break;
                            }
                        } else {
                            if (component.getContents() instanceof TranslatableContents translatableComponent && translatableComponent.getKey().equals("item.modifiers." + slotToAttributeMap.getKey().getName())) {
                                lines.add(++i, Component.literal(" ").append(Component.translatable("attribute.modifier.equals." + entry.getValue().getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(scaledAmount), Component.translatable(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private static double getScaledAttributeAmount(Attribute attribute, AttributeModifier attributeModifier, double attributeAmount) {
        // apply same scaling to attribute value as is done by vanilla for the tooltip
        if (attributeModifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributeModifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
            if (attribute.equals(Attributes.KNOCKBACK_RESISTANCE)) {
                return attributeAmount * 10.0D;
            } else {
                return attributeAmount;
            }
        } else {
            return attributeAmount * 100.0D;
        }
    }

    private static int removeAllAttributes(List<Component> lines) {
        int startIndex = findAttributesStart(lines);
        if (startIndex >= 0) {
            int endIndex = findAttributesEnd(lines);
            if (startIndex < endIndex) {
                // remove start to end, both inclusive, therefore +1
                for (int i = 0; i < endIndex - startIndex + 1; i++) {
                    lines.remove(startIndex);
                }
                // return start index when removal was successful for further processing
                return startIndex;
            }
        }
        return -1;
    }

    private static void addOldStyleAttributes(List<Component> lines, ItemStack stack, @Nullable Player player, int startIndex) {
        Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> attributesBySlot = getAttributesBySlot(stack);
        for (Map.Entry<EquipmentSlot, Multimap<Attribute, AttributeModifier>> entry : attributesBySlot.entrySet()) {
            List<Component> tmpList = Lists.newArrayList();
            addAttributesToTooltip(tmpList, player, stack, entry.getValue());
            if (!tmpList.isEmpty()) {
                tmpList.add(0, CommonComponents.EMPTY);
                if (attributesBySlot.size() > 1) {
                    tmpList.add(1, Component.translatable("item.modifiers." + entry.getKey().getName()).withStyle(ChatFormatting.GRAY));
                }
                lines.addAll(startIndex, tmpList);
                startIndex += tmpList.size();
            }
        }
    }

    private static int findAttributesStart(List<Component> lines) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).getContents() instanceof TranslatableContents contents && contents.getKey().startsWith("item.modifiers.")) {
                // attributes always have a blank line above, we include that
                return i - 1;
            }
        }
        return -1;
    }

    private static int findAttributesEnd(List<Component> lines) {
        int index = -1;
        for (int i = 0; i < lines.size(); i++) {
            final Component component = lines.get(i);
            TranslatableContents translatableComponent = null;
            if (component.getContents() instanceof TranslatableContents translatableComponent1) {
                translatableComponent = translatableComponent1;
            } else if (component.getContents() instanceof LiteralContents textComponent && textComponent.text().equals(" ")) {
                if (!component.getSiblings().isEmpty() && component.getSiblings().get(0).getContents() instanceof TranslatableContents translatableComponent1) {
                    translatableComponent = translatableComponent1;
                }
            }
            if (translatableComponent != null && translatableComponent.getKey().startsWith("attribute.modifier.")) {
                index = i;
            }
        }
        return index;
    }

    private static Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> getAttributesBySlot(ItemStack stack) {
        final Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> map = Maps.newLinkedHashMap();
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            Multimap<Attribute, AttributeModifier> multimap = stack.getAttributeModifiers(equipmentSlot);
            if (!multimap.isEmpty()) map.put(equipmentSlot, multimap);
        }
        return map;
    }

    private static double calculateAttributeValue(@Nullable Player player, Attribute attribute, Collection<AttributeModifier> modifiers) {

        double baseValue = player != null ? player.getAttributeBaseValue(attribute) : 0.0;
        Map<AttributeModifier.Operation, List<AttributeModifier>> modifiersByOperation = modifiers.stream().collect(Collectors.groupingBy(AttributeModifier::getOperation));

        for (AttributeModifier attributeModifier : modifiersByOperation.getOrDefault(AttributeModifier.Operation.ADDITION, List.of())) {
            baseValue += attributeModifier.getAmount();
        }

        double multipliedValue = baseValue;

        for (AttributeModifier attributeModifier : modifiersByOperation.getOrDefault(AttributeModifier.Operation.MULTIPLY_BASE, List.of())) {
            multipliedValue += baseValue * attributeModifier.getAmount();
        }

        for (AttributeModifier attributeModifier : modifiersByOperation.getOrDefault(AttributeModifier.Operation.MULTIPLY_TOTAL, List.of())) {
            multipliedValue *= 1.0D + attributeModifier.getAmount();
        }

        return attribute.sanitizeValue(multipliedValue);
    }

    private static void addAttributesToTooltip(List<Component> lines, @Nullable Player player, ItemStack stack, Multimap<Attribute, AttributeModifier> multimap) {

        for (Attribute attribute : multimap.keySet()) {

            double attributeValue = calculateAttributeValue(player, attribute, multimap.get(attribute));

            if (attribute == Attributes.ATTACK_DAMAGE) attributeValue += EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
            if (attribute == CommonAbstractions.INSTANCE.getAttackRangeAttribute() && !ModLoaderEnvironment.INSTANCE.isForge()) attributeValue += 3.0;
            if (attribute instanceof RangedAttribute rangedAttribute && rangedAttribute.getMaxValue() < 10.0) attributeValue *= 10.0 / rangedAttribute.getMaxValue();

            if (attributeValue > 0.0) {
                lines.add(Component.translatable("attribute.modifier.plus.0", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(attributeValue), Component.translatable(attribute.getDescriptionId())).withStyle(ChatFormatting.BLUE));
            } else if (attributeValue < 0.0) {
                // make value positive, adding a minus sign is handled by the translation string
                attributeValue *= -1.0;
                lines.add(Component.translatable("attribute.modifier.take.0", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(attributeValue), Component.translatable(attribute.getDescriptionId())).withStyle(ChatFormatting.RED));
            }
        }
    }
}
