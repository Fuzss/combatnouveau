package fuzs.combatnouveau.helper;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.combatnouveau.services.CommonAbstractions;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class SweepAttackHelper {

    public static boolean isSweepAttackPossible(Player player) {
        if (player.getAttackStrengthScale(0.5F) == 1.0F && player.onGround()
                && player.getKnownMovement().horizontalDistanceSqr() < Mth.square(player.getSpeed() * 2.5F)) {
            float attackDamage = (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            return attackDamage > 0.0F && isSweepingItem(player);
        } else {
            return false;
        }
    }

    private static boolean isSweepingItem(Player player) {
        if (CombatNouveau.CONFIG.get(ServerConfig.class).noSweepingWhenSneaking && player.isShiftKeyDown()) {
            return false;
        } else if (CombatNouveau.CONFIG.get(ServerConfig.class).requireSweepingEdge) {
            return player.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) > 0.0F;
        } else {
            return ToolTypeHelper.INSTANCE.isSword(player.getItemInHand(InteractionHand.MAIN_HAND));
        }
    }

    public static void airSweepAttack(Player player) {
        double moveX = (double) (-Mth.sin(player.getYRot() * ((float) Math.PI / 180))) * 2.0;
        double moveZ = (double) Mth.cos(player.getYRot() * ((float) Math.PI / 180)) * 2.0;
        AABB aABB = CommonAbstractions.INSTANCE.getSweepHitBox(player, player).move(moveX, 0.0, moveZ);
        float attackDamage = (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        sweepAttack(player, aABB, attackDamage, null);
        // This also resets the attack ticker.
        player.swing(InteractionHand.MAIN_HAND);
    }

    private static void sweepAttack(Player player, AABB aABB, float attackDamage, @Nullable Entity target) {
        player.level()
                .playSound(null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP,
                        player.getSoundSource(),
                        1.0f,
                        1.0f);

        if (player.level() instanceof ServerLevel serverLevel) {
            float sweepingAttackDamage =
                    1.0F + (float) player.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) * attackDamage;
            List<LivingEntity> list = serverLevel.getEntitiesOfClass(LivingEntity.class, aABB);
            for (LivingEntity livingEntity : list) {
                if (livingEntity != player && livingEntity != target && !player.isAlliedTo(livingEntity) && (
                        !(livingEntity instanceof ArmorStand) || !((ArmorStand) livingEntity).isMarker())
                        && player.distanceToSqr(livingEntity) < 9.0) {
                    DamageSource damageSource = player.damageSources().playerAttack(player);
                    float enchantedDamage = player.getEnchantedDamage(livingEntity, sweepingAttackDamage, damageSource);
                    if (livingEntity.hurtServer(serverLevel, damageSource, enchantedDamage)) {
                        livingEntity.knockback(0.4F,
                                Mth.sin(player.getYRot() * (float) (Math.PI / 180.0)),
                                -Mth.cos(player.getYRot() * (float) (Math.PI / 180.0)));
                        EnchantmentHelper.doPostAttackEffects(serverLevel, livingEntity, damageSource);
                    }
                }
            }

            double offsetX = -Mth.sin(player.getYRot() * ((float) Math.PI / 180F));
            double offsetZ = Mth.cos(player.getYRot() * ((float) Math.PI / 180F));
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                    player.getX() + offsetX,
                    player.getY(0.5F),
                    player.getZ() + offsetZ,
                    0,
                    offsetX,
                    0.0F,
                    offsetZ,
                    0.0F);
        }
    }
}
