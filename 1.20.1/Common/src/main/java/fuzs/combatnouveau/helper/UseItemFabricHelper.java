package fuzs.combatnouveau.helper;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class UseItemFabricHelper {

    public static InteractionResult useItem(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand) {
        int stackCount = stack.getCount();
        int stackDamage = stack.getDamageValue();
        InteractionResultHolder<ItemStack> interactionResultHolder = stack.use(level, player, hand);
        ItemStack itemStack = interactionResultHolder.getObject();
        if (itemStack == stack && itemStack.getCount() == stackCount && itemStack.getUseDuration() <= 0 &&
                itemStack.getDamageValue() == stackDamage) {
            return interactionResultHolder.getResult();
        } else if (interactionResultHolder.getResult() == InteractionResult.FAIL && itemStack.getUseDuration() > 0 &&
                !player.isUsingItem()) {
            return interactionResultHolder.getResult();
        } else {
            if (stack != itemStack) {
                player.setItemInHand(hand, itemStack);
            }

            if (player.isCreative() && itemStack != ItemStack.EMPTY) {
                itemStack.setCount(stackCount);
                if (itemStack.isDamageableItem() && itemStack.getDamageValue() != stackDamage) {
                    itemStack.setDamageValue(stackDamage);
                }
            }

            if (itemStack.isEmpty()) {
                player.setItemInHand(hand, ItemStack.EMPTY);
            }

            if (!player.isUsingItem()) {
                player.inventoryMenu.sendAllDataToRemote();
            }

            return interactionResultHolder.getResult();
        }
    }
}
