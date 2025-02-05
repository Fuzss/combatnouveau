package fuzs.combatnouveau.mixin.client;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ClientConfig;
import fuzs.combatnouveau.handler.AttackAttributeHandler;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
abstract class ItemStackMixin {

    @ModifyVariable(method = "addModifierTooltip", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private boolean addModifierTooltip(boolean isBaseAttributeModifierId, Consumer<Component> tooltipAdder, @Nullable Player player, Holder<Attribute> attribute, AttributeModifier modifier) {
        return isBaseAttributeModifierId || CombatNouveau.CONFIG.get(
                ClientConfig.class).specialBaseAttributeModifiers && modifier.amount() != 0.0 &&
                AttackAttributeHandler.BASE_ATTRIBUTE_MODIFIER_IDS.contains(modifier.id());
    }
}
