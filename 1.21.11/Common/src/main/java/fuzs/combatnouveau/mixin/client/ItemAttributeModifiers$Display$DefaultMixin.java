package fuzs.combatnouveau.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ClientConfig;
import fuzs.combatnouveau.handler.AttackAttributeHandler;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Consumer;

@Mixin(ItemAttributeModifiers.Display.Default.class)
abstract class ItemAttributeModifiers$Display$DefaultMixin {

    @ModifyVariable(method = "apply", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    public boolean apply(boolean isBaseAttributeModifierId, Consumer<Component> tooltipAdder, @Nullable Player player, Holder<Attribute> attribute, AttributeModifier modifier, @Local(
            ordinal = 0) LocalDoubleRef modifierAmount) {
        if (!CombatNouveau.CONFIG.get(ClientConfig.class).specialBaseAttributeModifiers) {
            return isBaseAttributeModifierId;
        }

        if (!isBaseAttributeModifierId && player != null && modifierAmount.get() != 0.0) {
            if (AttackAttributeHandler.BASE_ATTRIBUTE_MODIFIER_IDS.contains(modifier.id())) {
                if (player.getAttribute(attribute) != null) {
                    modifierAmount.set(modifierAmount.get() + player.getAttributeBaseValue(attribute));
                }

                return true;
            }
        }

        return isBaseAttributeModifierId;
    }
}
