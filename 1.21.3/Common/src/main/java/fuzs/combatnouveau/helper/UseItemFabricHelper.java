package fuzs.combatnouveau.helper;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class UseItemFabricHelper {

    public static InteractionResult useItem(ServerPlayer player, Level level, ItemStack itemStack, InteractionHand interactionHand) {
        int stackCount = itemStack.getCount();
        int stackDamage = itemStack.getDamageValue();
        InteractionResultHolder<ItemStack> holder = itemStack.use(level, player, interactionHand);
        ItemStack interactionResult = holder.getObject();
        if (interactionResult == itemStack && interactionResult.getCount() == stackCount && interactionResult.getUseDuration(player) <= 0 &&
                interactionResult.getDamageValue() == stackDamage) {
            return holder.getResult();
        } else if (holder.getResult() == InteractionResult.FAIL && interactionResult.getUseDuration(player) > 0 &&
                !player.isUsingItem()) {
            return holder.getResult();
        } else {
            if (itemStack != interactionResult) {
                player.setItemInHand(interactionHand, interactionResult);
            }

            if (player.isCreative() && interactionResult != ItemStack.EMPTY) {
                interactionResult.setCount(stackCount);
                if (interactionResult.isDamageableItem() && interactionResult.getDamageValue() != stackDamage) {
                    interactionResult.setDamageValue(stackDamage);
                }
            }

            if (interactionResult.isEmpty()) {
                player.setItemInHand(interactionHand, ItemStack.EMPTY);
            }

            if (!player.isUsingItem()) {
                player.inventoryMenu.sendAllDataToRemote();
            }

            return holder.getResult();
        }
    }
}
