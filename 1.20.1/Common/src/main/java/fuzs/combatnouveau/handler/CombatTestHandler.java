package fuzs.combatnouveau.handler;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.client.helper.UseItemFabricClientHelper;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.combatnouveau.helper.UseItemFabricHelper;
import fuzs.combatnouveau.mixin.accessor.ItemAccessor;
import fuzs.combatnouveau.mixin.accessor.PlayerAccessor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class CombatTestHandler {
    
    public static void onStartPlayerTick(Player player) {
        if (!CombatNouveau.CONFIG.get(ServerConfig.class).fastSwitching) return;
        // switching items no longer triggers the attack cooldown
        ItemStack stack = player.getMainHandItem();
        if (!ItemStack.matches(((PlayerAccessor) player).goldenagecombat$getLastItemInMainHand(), stack)) {
            ((PlayerAccessor) player).goldenagecombat$setLastItemInMainHand(stack.copy());
        }
    }

    public static EventResult onUseItemStart(LivingEntity entity, ItemStack stack, MutableInt remainingUseDuration) {
        if (CombatNouveau.CONFIG.get(ServerConfig.class).removeShieldDelay && stack.getUseAnimation() == UseAnim.BLOCK) {
            // remove shield activation delay
            remainingUseDuration.accept(stack.getUseDuration() - 5);
        }
        if (CombatNouveau.CONFIG.get(ServerConfig.class).fastDrinking && stack.getUseAnimation() == UseAnim.DRINK) {
            remainingUseDuration.accept(20);
        }
        return EventResult.PASS;
    }

    public static EventResultHolder<InteractionResult> onUseItem(Player player, Level level, InteractionHand hand) {
        if (!CombatNouveau.CONFIG.get(ServerConfig.class).throwablesDelay) return EventResultHolder.pass();
        ItemStack itemInHand = player.getItemInHand(hand);
        if (itemInHand.getItem() instanceof SnowballItem || itemInHand.getItem() instanceof EggItem) {
            // add delay after using an item
            player.getCooldowns().addCooldown(itemInHand.getItem(), 4);
            // the callback runs before cooldowns on Fabric, so we need to perform the interaction ourselves and cancel the callback
            if (!ModLoaderEnvironment.INSTANCE.getModLoader().isForge()) {
                InteractionResult result;
                if (level.isClientSide) {
                    result = UseItemFabricClientHelper.useItem(player, hand);
                } else {
                    result = UseItemFabricHelper.useItem((ServerPlayer) player, level, itemInHand, hand);
                }
                return EventResultHolder.interrupt(result);
            }
        }
        return EventResultHolder.pass();
    }

    public static EventResult onLivingHurt(LivingEntity entity, DamageSource source, MutableFloat amount) {
        if (CombatNouveau.CONFIG.get(ServerConfig.class).eatingInterruption) {
            UseAnim useAction = entity.getUseItem().getUseAnimation();
            if (useAction == UseAnim.EAT || useAction == UseAnim.DRINK) {
                entity.stopUsingItem();
            }
        }
        if (CombatNouveau.CONFIG.get(ServerConfig.class).noProjectileImmunity) {
            if (source.is(DamageTypeTags.IS_PROJECTILE)) {
                // immediately reset damage immunity after being hit by any projectile, fixes multishot
                entity.invulnerableTime = 0;
            }
        }
        return EventResult.PASS;
    }

    public static void setMaxStackSize(ServerConfig serverConfig) {
        if (!serverConfig.increaseStackSize) return;
        ((ItemAccessor) Items.SNOWBALL).goldenagecombat$setMaxStackSize(64);
        ((ItemAccessor) Items.EGG).goldenagecombat$setMaxStackSize(64);
        ((ItemAccessor) Items.POTION).goldenagecombat$setMaxStackSize(16);
    }
}
