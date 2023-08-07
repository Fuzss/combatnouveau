package fuzs.combatnouveau.handler;

import fuzs.combatnouveau.CombatNouveau;
import fuzs.combatnouveau.config.ServerConfig;
import fuzs.combatnouveau.core.CommonAbstractions;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
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

public class SweepAttackHandler {

    public static void attackAir(Player player) {
        double walkDist = player.walkDist - player.walkDistO;
        if (!player.onGround() || !(walkDist < player.getSpeed())) return;
        float attackDamage = (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        if (attackDamage > 0.0f && player.getAttackStrengthScale(0.5F) > 0.9F && checkSweepAttack(player)) {
            boolean hasFarPickRange = player.isCreative();
            double attackReach = CommonAbstractions.INSTANCE.getCurrentAttackReach(player, hasFarPickRange);
            double moveX = (double) (-Mth.sin(player.getYRot() * ((float) Math.PI / 180))) * 2.0;
            double moveZ = (double) Mth.cos(player.getYRot() * ((float) Math.PI / 180)) * 2.0;
            AABB aABB = CommonAbstractions.INSTANCE.getSweepHitBox(player, player).move(moveX, 0.0, moveZ);
            sweepAttack(player, aABB, attackReach, attackDamage, null);
        }
        // also resets attack ticker
        player.swing(InteractionHand.MAIN_HAND);
    }

    public static void sweepAttack(Player player, AABB aABB, double currentAttackReach, float baseAttackDamage, @Nullable Entity target) {
        float h = 1.0f + EnchantmentHelper.getSweepingDamageRatio(player) * baseAttackDamage;
        List<LivingEntity> list = player.level().getEntitiesOfClass(LivingEntity.class, aABB);
        for (LivingEntity livingEntity : list) {
            if (livingEntity == player || livingEntity == target || player.isAlliedTo(livingEntity) || livingEntity instanceof ArmorStand && ((ArmorStand) livingEntity).isMarker()) {
                continue;
            }
            double d = currentAttackReach + livingEntity.getBbWidth() * 0.5;
            if (!(player.distanceToSqr(livingEntity) < d * d)) continue;
            livingEntity.knockback(0.4f, Mth.sin(player.getYRot() * ((float) Math.PI / 180)), -Mth.cos(player.getYRot() * ((float) Math.PI / 180)));
            livingEntity.hurt(player.damageSources().playerAttack(player), h);
        }
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0f, 1.0f);
        if (player.level() instanceof ServerLevel) {
            double d = -Mth.sin(player.getYRot() * ((float) Math.PI / 180));
            double i = Mth.cos(player.getYRot() * ((float) Math.PI / 180));
            ((ServerLevel) player.level()).sendParticles(ParticleTypes.SWEEP_ATTACK, player.getX() + d, player.getY() + (double) player.getBbHeight() * 0.5, player.getZ() + i, 0, d, 0.0, i, 0.0);
        }
    }

    public static boolean checkSweepAttack(Player player) {
        if (CombatNouveau.CONFIG.get(ServerConfig.class).combatTests.noSneakSweeping && player.isShiftKeyDown()) {
            return false;
        }
        if (CombatNouveau.CONFIG.get(ServerConfig.class).combatTests.sweepingRequired) {
            return EnchantmentHelper.getSweepingDamageRatio(player) > 0.0f;
        }
        ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        return ToolTypeHelper.INSTANCE.isSword(itemInHand);
    }
}
