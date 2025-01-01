package fuzs.combatnouveau.helper;

import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class UseItemFabricHelper {

    public static InteractionResult useItem(ServerPlayer player, Level level, ItemStack itemStack, InteractionHand interactionHand) {
        int itemStackCount = itemStack.getCount();
        int itemStackDamage = itemStack.getDamageValue();
        InteractionResult interactionResult = itemStack.use(level, player, interactionHand);
        ItemStack itemStackResult = InteractionResultHelper.getObject(interactionResult);
        if (itemStackResult == itemStack && itemStackResult.getCount() == itemStackCount &&
                itemStackResult.getUseDuration(player) <= 0 && itemStackResult.getDamageValue() == itemStackDamage) {
            return interactionResult;
        } else if (interactionResult == InteractionResult.FAIL && itemStackResult.getUseDuration(player) > 0 &&
                !player.isUsingItem()) {
            return interactionResult;
        } else {
            if (itemStack != itemStackResult) {
                player.setItemInHand(interactionHand, itemStackResult);
            }

            if (player.isCreative() && itemStackResult != ItemStack.EMPTY) {
                itemStackResult.setCount(itemStackCount);
                if (itemStackResult.isDamageableItem() && itemStackResult.getDamageValue() != itemStackDamage) {
                    itemStackResult.setDamageValue(itemStackDamage);
                }
            }

            if (itemStackResult.isEmpty()) {
                player.setItemInHand(interactionHand, ItemStack.EMPTY);
            }

            if (!player.isUsingItem()) {
                player.inventoryMenu.sendAllDataToRemote();
            }

            return interactionResult;
        }
    }
}
