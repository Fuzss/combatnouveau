package fuzs.combatnouveau.helper;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.combatnouveau.core.CommonAbstractions;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SweepAttackHelper {

    public static void airSweepAttack(Player player) {
        if (player.getAttackStrengthScale(0.5F) == 1.0F) {
            double walkDist = player.walkDist - player.walkDistO;
            if (!player.onGround() || !(walkDist < player.getSpeed())) return;
            float attackDamage = (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            if (attackDamage > 0.0F && allowSweepAttack(player)) {
                double moveX = (double) (-Mth.sin(player.getYRot() * ((float) Math.PI / 180))) * 2.0;
                double moveZ = (double) Mth.cos(player.getYRot() * ((float) Math.PI / 180)) * 2.0;
                AABB aABB = CommonAbstractions.INSTANCE.getSweepHitBox(player, player).move(moveX, 0.0, moveZ);
                sweepAttack(player, aABB, attackDamage, null);
            }
            // also resets attack ticker
            player.swing(InteractionHand.MAIN_HAND);
        }
    }

    private static boolean allowSweepAttack(Player player) {
        if (CombatNouveau.CONFIG.get(ServerConfig.class).noSweepingWhenSneaking && player.isShiftKeyDown()) {
            return false;
        } else if (CombatNouveau.CONFIG.get(ServerConfig.class).requireSweepingEdge) {
            return player.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) > 0.0F;
        } else {
            ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            return ToolTypeHelper.INSTANCE.isSword(itemInHand);
        }
    }

    private static void sweepAttack(Player player, AABB aABB, float attackDamage, @Nullable Entity target) {
        float sweepingAttackDamage = 1.0F + (float) player.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) * attackDamage;
        List<LivingEntity> list = player.level().getEntitiesOfClass(LivingEntity.class, aABB);
        for (LivingEntity livingEntity : list) {
            if (livingEntity != player
                    && livingEntity != target
                    && !player.isAlliedTo(livingEntity)
                    && (!(livingEntity instanceof ArmorStand) || !((ArmorStand) livingEntity).isMarker())
                    && player.distanceToSqr(livingEntity) < 9.0) {
                DamageSource damageSource = player.damageSources().playerAttack(player);
                float enchantedDamage = player.getEnchantedDamage(livingEntity, sweepingAttackDamage, damageSource);
                livingEntity.knockback(
                        0.4F, (double)Mth.sin(player.getYRot() * (float) (Math.PI / 180.0)), (double)(-Mth.cos(player.getYRot() * (float) (Math.PI / 180.0)))
                );
                livingEntity.hurt(damageSource, enchantedDamage);
                if (player.level() instanceof ServerLevel serverLevel) {
                    EnchantmentHelper.doPostAttackEffects(serverLevel, livingEntity, damageSource);
                }
            }
        }
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0f, 1.0f);
        player.sweepAttack();
    }
}
