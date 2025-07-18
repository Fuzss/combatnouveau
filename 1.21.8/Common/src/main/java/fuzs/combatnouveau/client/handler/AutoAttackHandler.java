package fuzs.combatnouveau.client.handler;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.HitResult;

public class AutoAttackHandler {
    private static int autoAttackDelay;

    public static void onStartTick(Minecraft minecraft) {
        if (minecraft.player != null && minecraft.player.getAttackStrengthScale(0.5F) < 1.0F) {
            resetAutoAttackDelay();
        } else if (autoAttackDelay > 0) {
            autoAttackDelay--;
        }
    }

    public static void resetAutoAttackDelay() {
        autoAttackDelay = CombatNouveau.CONFIG.get(ServerConfig.class).holdAttackButtonDelay;
    }

    public static boolean readyForAutoAttack() {
        return autoAttackDelay == 0;
    }

    public static EventResult onAttackInteraction(Minecraft minecraft, LocalPlayer player, HitResult hitResult) {
        if (minecraft.hitResult.getType() != HitResult.Type.BLOCK) {
            // cancel attack when attack cooldown is not completely recharged
            if (player.getAttackStrengthScale(0.5F) < CombatNouveau.CONFIG.get(ServerConfig.class).minAttackStrength) {
                return EventResult.INTERRUPT;
            }
        }
        return EventResult.PASS;
    }
}
