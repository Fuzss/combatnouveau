package fuzs.combatnouveau.mixin;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.puzzleslib.api.item.v2.ItemHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DiggerItem.class)
abstract class DiggerItemMixin extends Item {

    public DiggerItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "hurtEnemy", at = @At("HEAD"), cancellable = true)
    public void hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> callback) {
        if (!CombatNouveau.CONFIG.get(ServerConfig.class).noAxeAttackPenalty) return;
        ItemHelper.hurtAndBreak(itemStack, 1, attacker, EquipmentSlot.MAINHAND);
        callback.setReturnValue(true);
    }
}
