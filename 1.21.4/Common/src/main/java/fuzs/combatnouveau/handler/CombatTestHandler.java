package fuzs.combatnouveau.handler;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.client.helper.UseItemFabricClientHelper;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.combatnouveau.helper.UseItemFabricHelper;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.level.Level;

public class CombatTestHandler {

    public static void onStartPlayerTick(Player player) {
        if (!CombatNouveau.CONFIG.get(ServerConfig.class).fastSwitching) return;
        // switching items no longer triggers the attack cooldown
        ItemStack mainHandItem = player.getMainHandItem();
        if (!ItemStack.matches(player.lastItemInMainHand, mainHandItem)) {
            player.lastItemInMainHand = mainHandItem.copy();
        }
    }

    public static EventResult onUseItemStart(LivingEntity livingEntity, ItemStack itemStack, MutableInt remainingUseDuration) {
        if (CombatNouveau.CONFIG.get(ServerConfig.class).removeShieldDelay &&
                itemStack.getUseAnimation() == ItemUseAnimation.BLOCK) {
            // remove shield activation delay
            remainingUseDuration.accept(itemStack.getUseDuration(livingEntity) - 5);
        }
        if (CombatNouveau.CONFIG.get(ServerConfig.class).fastDrinking &&
                itemStack.getUseAnimation() == ItemUseAnimation.DRINK) {
            remainingUseDuration.accept(20);
        }

        return EventResult.PASS;
    }

    public static EventResultHolder<InteractionResult> onUseItem(Player player, Level level, InteractionHand interactionHand) {
        if (!CombatNouveau.CONFIG.get(ServerConfig.class).throwablesDelay) return EventResultHolder.pass();
        ItemStack itemInHand = player.getItemInHand(interactionHand);
        if (itemInHand.getItem() instanceof SnowballItem || itemInHand.getItem() instanceof EggItem) {
            // add delay after using an item
            player.getCooldowns().addCooldown(itemInHand, 4);
            // the callback runs before cooldowns on Fabric, so we need to perform the interaction ourselves and cancel the callback
            if (ModLoaderEnvironment.INSTANCE.getModLoader().isFabricLike()) {
                InteractionResult result;
                if (level.isClientSide) {
                    result = UseItemFabricClientHelper.useItem(player, interactionHand);
                } else {
                    result = UseItemFabricHelper.useItem((ServerPlayer) player, level, itemInHand, interactionHand);
                }

                return EventResultHolder.interrupt(result);
            }
        }

        return EventResultHolder.pass();
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

    public static EventResult onShieldBlock(LivingEntity blockingEntity, DamageSource damageSource, float damageAmount) {
        if (CombatNouveau.CONFIG.get(ServerConfig.class).shieldKnockback == ServerConfig.ShieldKnockback.NONE) {
            return EventResult.PASS;
        }
        if (damageSource.isDirect() && damageSource.getEntity() instanceof LivingEntity attackingEntity) {
            double knockBackStrength;
            if (CombatNouveau.CONFIG.get(ServerConfig.class).shieldKnockback == ServerConfig.ShieldKnockback.VARIABLE) {
                int variableShieldKnockbackDelay = CombatNouveau.CONFIG.get(ServerConfig.class).variableShieldKnockbackDelay;
                if (!CombatNouveau.CONFIG.get(ServerConfig.class).removeShieldDelay) {
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
