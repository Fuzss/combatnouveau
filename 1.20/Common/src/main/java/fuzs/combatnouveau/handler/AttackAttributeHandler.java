package fuzs.combatnouveau.handler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.combatnouveau.core.CommonAbstractions;
import fuzs.combatnouveau.mixin.accessor.ItemAccessor;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;

import java.util.Map;
import java.util.UUID;

public class AttackAttributeHandler {
    public static final UUID BASE_ATTACK_DAMAGE_UUID = ItemAccessor.goldenagecombat$getBaseAttackDamageUUID();
    public static final UUID BASE_ATTACK_SPEED_UUID = ItemAccessor.goldenagecombat$getBaseAttackSpeedUUID();
    public static final UUID BASE_ATTACK_REACH_UUID = UUID.fromString("26cb07a3-209d-4110-8e10-1010243614c8");
    private static final String ATTACK_DAMAGE_MODIFIER_NAME = CombatNouveau.id("attack_damage_modifier").toString();
    private static final String ATTACK_SPEED_MODIFIER_NAME = CombatNouveau.id("attack_speed_modifier").toString();
    private static final String ATTACK_RANGE_MODIFIER_NAME = CombatNouveau.id("attack_range_modifier").toString();
    public static final Map<Class<?>, Double> ATTACK_RANGE_BONUS_OVERRIDES = ImmutableMap.of(TridentItem.class, 1.0, HoeItem.class, 1.0, SwordItem.class, 0.5, TieredItem.class, 0.0);

    public static void onItemAttributeModifiers(ItemStack stack, EquipmentSlot equipmentSlot, Multimap<Attribute, AttributeModifier> attributeModifiers) {
        if (!CombatNouveau.CONFIG.getHolder(ServerConfig.class).isAvailable()) return;
        // don't change items whose attributes have already been changed via the nbt tag
        if (equipmentSlot == EquipmentSlot.MAINHAND && (!stack.hasTag() || !stack.getTag().contains("AttributeModifiers", Tag.TAG_LIST))) {
            trySetNewAttributeValue(stack, attributeModifiers, Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE_UUID, ATTACK_DAMAGE_MODIFIER_NAME, CombatNouveau.CONFIG.get(ServerConfig.class).attackDamageOverrides);
            trySetNewAttributeValue(stack, attributeModifiers, Attributes.ATTACK_SPEED, BASE_ATTACK_SPEED_UUID, ATTACK_SPEED_MODIFIER_NAME, CombatNouveau.CONFIG.get(ServerConfig.class).attackSpeedOverrides);
            if (!trySetNewAttributeValue(stack, attributeModifiers, CommonAbstractions.INSTANCE.getAttackRangeAttribute(), BASE_ATTACK_REACH_UUID, ATTACK_RANGE_MODIFIER_NAME, CombatNouveau.CONFIG.get(ServerConfig.class).attackReachOverrides)) {
                if (CombatNouveau.CONFIG.get(ServerConfig.class).additionalAttackReach) {
                    for (Map.Entry<Class<?>, Double> entry : ATTACK_RANGE_BONUS_OVERRIDES.entrySet()) {
                        if (entry.getKey().isInstance(stack.getItem())) {
                            setNewAttributeValue(attributeModifiers, CommonAbstractions.INSTANCE.getAttackRangeAttribute(), BASE_ATTACK_REACH_UUID, ATTACK_RANGE_MODIFIER_NAME, entry.getValue());
                            break;
                        }
                    }
                }
            }
        }
    }

    private static boolean trySetNewAttributeValue(ItemStack itemStack, Multimap<Attribute, AttributeModifier> attributeModifiers, Attribute attribute, UUID modifierUUID, String modifierName, ConfigDataSet<Item> attackDamageOverrides) {
        if (attackDamageOverrides.contains(itemStack.getItem())) {
            double newValue = attackDamageOverrides.<Double>getOptional(itemStack.getItem(), 0).orElseThrow();
            setNewAttributeValue(attributeModifiers, attribute, modifierUUID, modifierName, newValue);
            return true;
        }
        return false;
    }

    private static void setNewAttributeValue(Multimap<Attribute, AttributeModifier> attributeModifiers, Attribute attribute, UUID modifierUUID, String modifierName, double newValue) {
        attributeModifiers.removeAll(attribute);
        attributeModifiers.put(attribute, new AttributeModifier(modifierUUID, modifierName, newValue, AttributeModifier.Operation.ADDITION));
    }
}
