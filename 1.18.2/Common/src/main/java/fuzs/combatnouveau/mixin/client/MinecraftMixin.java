package fuzs.combatnouveau.mixin.client;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.client.handler.AttackAirHandler;
import fuzs.combatnouveau.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
abstract class MinecraftMixin {
    @Shadow
    public ClientLevel level;
    @Shadow
    public LocalPlayer player;

    @Inject(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;stopDestroyBlock()V", shift = At.Shift.AFTER))
    private void continueAttack(boolean attacking, CallbackInfo callback) {
        if (!CombatNouveau.CONFIG.get(ServerConfig.class).holdAttackButton) return;
        // do not cancel stopDestroyBlock as in combat snapshots
        // also additional check for an item being used
        if (attacking && !this.player.isUsingItem()) this.startAttack();
    }

    @Shadow
    private boolean startAttack() {
        throw new RuntimeException();
    }

    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V", shift = At.Shift.BEFORE), cancellable = true)
    private void startAttack(CallbackInfoReturnable<Boolean> callback) {
        if (CombatNouveau.CONFIG.get(ServerConfig.class).retainEnergyOnMiss) {
            AttackAirHandler.swingHandRetainAttackStrength(this.player, InteractionHand.MAIN_HAND);
            callback.setReturnValue(false);
        }
        AttackAirHandler.onLeftClickEmpty();
    }
}
