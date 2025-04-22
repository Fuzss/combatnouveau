package fuzs.combatnouveau.handler;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.CommonConfig;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;

public class CombatTestHandler {

    public static void onStartPlayerTick(Player player) {
        if (!CombatNouveau.CONFIG.get(ServerConfig.class).fastSwitching) return;
        // switching items no longer triggers the attack cooldown
        ItemStack mainHandItem = player.getMainHandItem();
        if (!ItemStack.matches(player.lastItemInMainHand, mainHandItem)) {
            player.lastItemInMainHand = mainHandItem.copy();
        }
    }

    public static EventResult onLivingHurt(LivingEntity livingEntity, DamageSource damageSource, MutableFloat amount) {
        if (CombatNouveau.CONFIG.get(ServerConfig.class).eatingInterruption) {
            if (!damageSource.is(DamageTypeTags.BYPASSES_ARMOR)) {
                ItemUseAnimation useAction = livingEntity.getUseItem().getUseAnimation();
                if (useAction == ItemUseAnimation.EAT || useAction == ItemUseAnimation.DRINK) {
                    livingEntity.stopUsingItem();
                }
            }
        }
        if (CombatNouveau.CONFIG.get(ServerConfig.class).noProjectileImmunity) {
            if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) {
                // immediately reset damage immunity after being hit by any projectile, fixes multishot
                livingEntity.invulnerableTime = 0;
            }
        }

        return EventResult.PASS;
    }

    public static EventResult onShieldBlock(LivingEntity blockingEntity, DamageSource damageSource, DefaultedFloat blockedDamage) {
        if (CombatNouveau.CONFIG.get(ServerConfig.class).shieldKnockback == ServerConfig.ShieldKnockback.NONE) {
            return EventResult.PASS;
        }
        if (damageSource.isDirect() && damageSource.getEntity() instanceof LivingEntity attackingEntity) {
            double knockBackStrength;
            if (CombatNouveau.CONFIG.get(ServerConfig.class).shieldKnockback == ServerConfig.ShieldKnockback.VARIABLE) {
                int variableShieldKnockbackDelay = CombatNouveau.CONFIG.get(ServerConfig.class).variableShieldKnockbackDelay;
                if (!CombatNouveau.CONFIG.get(CommonConfig.class).removeShieldDelay) {
                    variableShieldKnockbackDelay += 5;
                }
                knockBackStrength = (blockingEntity.getUseItem().getUseDuration(blockingEntity) -
                        blockingEntity.getUseItemRemainingTicks()) / (double) variableShieldKnockbackDelay;
                knockBackStrength = 1.0 - Mth.clamp(knockBackStrength, 0.0, 1.0);
                knockBackStrength += 0.5;
            } else {
                knockBackStrength = 0.5;
            }
            attackingEntity.knockback(knockBackStrength + blockingEntity.getAttributeValue(Attributes.ATTACK_KNOCKBACK),
                    blockingEntity.getX() - attackingEntity.getX(),
                    blockingEntity.getZ() - attackingEntity.getZ());
        }

        return EventResult.PASS;
    }
}
