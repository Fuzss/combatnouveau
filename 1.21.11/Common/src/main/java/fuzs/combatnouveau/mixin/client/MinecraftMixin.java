package fuzs.combatnouveau.mixin.client;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.client.handler.AutoAttackHandler;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.combatnouveau.helper.SweepAttackHelper;
import fuzs.combatnouveau.network.client.ServerboundSweepAttackMessage;
import fuzs.combatnouveau.network.client.ServerboundSwingArmMessage;
import fuzs.puzzleslib.api.network.v4.MessageSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.jspecify.annotations.Nullable;
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
    @Shadow
    @Nullable
    public MultiPlayerGameMode gameMode;

    @Inject(method = "continueAttack",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;stopDestroyBlock()V",
                     shift = At.Shift.AFTER))
    private void continueAttack(boolean attacking, CallbackInfo callback) {
        if (!CombatNouveau.CONFIG.get(ServerConfig.class).holdAttackButton) {
            return;
        }

        // do not cancel stopDestroyBlock as in combat snapshots
        // also additional check for an item being used
        if (attacking && !this.player.isUsingItem()) {
            if (this.player.getAttackStrengthScale(0.5F) >= 1.0 && AutoAttackHandler.readyForAutoAttack()) {
                this.startAttack();
                AutoAttackHandler.resetAutoAttackDelay();
            }
        }
    }

    @Shadow
    private boolean startAttack() {
        throw new RuntimeException();
    }

    @Inject(method = "startAttack",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V"),
            cancellable = true)
    private void startAttack(CallbackInfoReturnable<Boolean> callback) {
        if (CombatNouveau.CONFIG.get(ServerConfig.class).retainEnergyOnMiss) {
            // finish executing Minecraft::startAttack without calling a reset on the attack strength ticker
            this.player.swing(InteractionHand.MAIN_HAND, false);
            MessageSender.broadcast(new ServerboundSwingArmMessage(InteractionHand.MAIN_HAND));
            callback.setReturnValue(false);
        }

        if (CombatNouveau.CONFIG.get(ServerConfig.class).airSweepAttack) {
            if (SweepAttackHelper.isSweepAttackPossible(this.player)) {
                this.gameMode.ensureHasSentCarriedItem();
                MessageSender.broadcast(new ServerboundSweepAttackMessage((this.player).isShiftKeyDown()));
                // possibly blocked by retainEnergyOnMiss option, we want it regardless in case of triggering a sweep attack
                this.player.resetAttackStrengthTicker();
            }
        }
    }
}
