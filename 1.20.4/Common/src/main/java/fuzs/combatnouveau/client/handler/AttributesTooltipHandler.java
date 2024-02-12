package fuzs.combatnouveau.client.handler;

import com.google.common.collect.Multimap;
import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.client.helper.AttributeTooltipHelper;
import fuzs.combatnouveau.config.ClientConfig;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.combatnouveau.core.CommonAbstractions;
import fuzs.combatnouveau.handler.AttackAttributeHandler;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AttributesTooltipHandler {
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    public static void onItemTooltip(ItemStack stack, @Nullable Player player, List<Component> lines, TooltipFlag context) {
        if (!CombatNouveau.CONFIG.getHolder(ServerConfig.class).isAvailable()) return;
        if (CombatNouveau.CONFIG.get(ServerConfig.class).additionalAttackReach && AttackAttributeHandler.ATTACK_RANGE_BONUS_OVERRIDES.keySet().stream().anyMatch(t -> t.isInstance(stack.getItem())) || CombatNouveau.CONFIG.get(ServerConfig.class).attackReachOverrides.contains(stack.getItem())) {
            convertToDefaultAttribute(lines, AttackAttributeHandler.BASE_ATTACK_REACH_UUID, stack, player);
        }
        if (CombatNouveau.CONFIG.get(ClientConfig.class).specialArmorAttributes && stack.getItem() instanceof ArmorItem item) {
            convertToDefaultAttribute(lines, ARMOR_MODIFIER_UUID_PER_SLOT[item.getType().getSlot().getIndex()], stack, player);
        }
    }

    private static void convertToDefaultAttribute(List<Component> lines, UUID attributeId, ItemStack stack, @Nullable Player player) {
        Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> attributesBySlot = AttributeTooltipHelper.getAttributesBySlot(stack);
        for (Map.Entry<EquipmentSlot, Multimap<Attribute, AttributeModifier>> slotToAttributeMap : attributesBySlot.entrySet()) {
            List<Map.Entry<Attribute, AttributeModifier>> attributeModifiers = Lists.newArrayList();
            for (Map.Entry<Attribute, AttributeModifier> attributeToModifier : slotToAttributeMap.getValue().entries()) {
                if (attributeToModifier.getValue().getId().equals(attributeId)) {
                    attributeModifiers.add(attributeToModifier);
                }
            }
            $1:
            for (Map.Entry<Attribute, AttributeModifier> entry : attributeModifiers) {
                Attribute attribute = entry.getKey();
                AttributeModifier attributeModifier = entry.getValue();
                double attributeAmount = getAdjustedAttributeAmount(stack, player, attribute, attributeModifier);
                if (attributeAmount != 0.0) {
                    int slotModifiersStart = -1;
                    for (int i = 0; i < lines.size(); i++) {
                        Component component = lines.get(i);
                        // try to find the attribute and replace it, if that fails append new attribute line at the beginning of the slot modifier section
                        if (AttributeTooltipHelper.matchesAttributeComponent(component, attribute, attributeModifier)) {
                            lines.set(i, Component.literal(" ").append(Component.translatable("attribute.modifier.equals." + attributeModifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(attributeAmount), Component.translatable(attribute.getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN));
                            continue $1;
                        } else if (component.getContents() instanceof TranslatableContents translatableComponent && translatableComponent.getKey().equals("item.modifiers." + slotToAttributeMap.getKey().getName())) {
                            slotModifiersStart = i + 1;
                        }
                    }
                    if (slotModifiersStart != -1) {
                        lines.add(slotModifiersStart, Component.literal(" ").append(Component.translatable("attribute.modifier.equals." + attributeModifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(attributeAmount), Component.translatable(attribute.getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN));
                    }
                }
            }
        }
    }

    /**
     * Adjusts a given value for an attribute modifier for the tooltip, similar to vanilla, but with better support for {@link RangedAttribute}s with small values.
     *
     * @param stack             the item stack
     * @param player            a player to get a base value from, otherwise <code>0.0</code> is used
     * @param attribute         the attribute the value is for
     * @param attributeModifier the modifier where the value comes from
     * @return the adjusted value, potentially still the original input
     */
    public static double getAdjustedAttributeAmount(ItemStack stack, @Nullable Player player, Attribute attribute, AttributeModifier attributeModifier) {
        double attributeAmount = attributeModifier.getAmount();
        if (player != null) attributeAmount += player.getAttributes().hasAttribute(attribute) ? player.getAttributeBaseValue(attribute) : 0.0;
        if (attribute == Attributes.ATTACK_DAMAGE)
            attributeAmount += EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
        if (attribute == CommonAbstractions.INSTANCE.getAttackRangeAttribute() && !ModLoaderEnvironment.INSTANCE.getModLoader().isForgeLike())
            attributeAmount += 3.0;
        if (attributeModifier.getOperation() == AttributeModifier.Operation.ADDITION) {
            if (attribute instanceof RangedAttribute rangedAttribute && rangedAttribute.getMaxValue() < 10.0) {
                attributeAmount *= 10.0 / rangedAttribute.getMaxValue();
            }
        } else {
            attributeAmount *= 100.0;
        }
        return attributeAmount;
    }
}
