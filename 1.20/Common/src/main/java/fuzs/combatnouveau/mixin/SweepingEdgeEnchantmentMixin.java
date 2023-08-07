package fuzs.combatnouveau.mixin;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ServerConfig;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SweepingEdgeEnchantment.class)
abstract class SweepingEdgeEnchantmentMixin {

    @Inject(method = "getSweepingDamageRatio", at = @At("TAIL"), cancellable = true)
    private static void getSweepingDamageRatio(int level, CallbackInfoReturnable<Float> callback) {
        if (!CombatNouveau.CONFIG.get(ServerConfig.class).combatTests.halfSweepingDamage) return;
        callback.setReturnValue(callback.getReturnValueF() * 0.5F);
    }
}
