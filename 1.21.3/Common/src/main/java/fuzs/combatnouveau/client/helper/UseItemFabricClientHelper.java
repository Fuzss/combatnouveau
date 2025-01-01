package fuzs.combatnouveau.client.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableObject;

public class UseItemFabricClientHelper {

    public static InteractionResult useItem(Player player, InteractionHand interactionHand) {
        Minecraft minecraft = Minecraft.getInstance();
        // we need to send this packet since Fabric Api hooks in disrupting the method call, so the packet is not actually send when the callback is cancelled
        minecraft.getConnection()
                .send(new ServerboundMovePlayerPacket.PosRot(player.getX(), player.getY(), player.getZ(),
                        player.getYRot(), player.getXRot(), player.onGround()
                ));
        MutableObject<InteractionResult> mutableObject = new MutableObject<>();
        minecraft.gameMode.startPrediction(minecraft.level, (i) -> {
            ServerboundUseItemPacket serverboundUseItemPacket = new ServerboundUseItemPacket(interactionHand, i, player.getYRot(), player.getXRot());
            ItemStack itemInHand = player.getItemInHand(interactionHand);
            InteractionResultHolder<ItemStack> result = itemInHand.use(minecraft.level, player, interactionHand);
            ItemStack itemStack = result.getObject();
            if (itemStack != itemInHand) {
                player.setItemInHand(interactionHand, itemStack);
            }

            mutableObject.setValue(result.getResult());
            return serverboundUseItemPacket;
        });
        return mutableObject.getValue();
    }
}
