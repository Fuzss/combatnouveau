package fuzs.combatnouveau.network.client;

import fuzs.puzzleslib.api.network.v4.codec.ExtraStreamCodecs;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;

public record ServerboundSwingArmMessage(InteractionHand interactionHand) implements ServerboundPlayMessage {
    public static final StreamCodec<ByteBuf, ServerboundSwingArmMessage> STREAM_CODEC = StreamCodec.composite(
            ExtraStreamCodecs.fromEnum(InteractionHand.class),
            ServerboundSwingArmMessage::interactionHand,
            ServerboundSwingArmMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                context.player().swing(ServerboundSwingArmMessage.this.interactionHand, false);
            }
        };
    }
}
