package fuzs.combatnouveau.network.client;

import fuzs.combatnouveau.helper.SweepAttackHelper;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public record ServerboundSweepAttackMessage(boolean isUsingSecondaryAction) implements ServerboundPlayMessage {
    public static final StreamCodec<ByteBuf, ServerboundSweepAttackMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            ServerboundSweepAttackMessage::isUsingSecondaryAction,
            ServerboundSweepAttackMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                // mimics behavior of ServerboundInteractPacket as that one is used in combat tests
                ServerPlayer player = context.player();
                player.setShiftKeyDown(ServerboundSweepAttackMessage.this.isUsingSecondaryAction);
                if (player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
                    if (SweepAttackHelper.isSweepAttackPossible(player)) {
                        SweepAttackHelper.airSweepAttack(player);
                    }
                }
            }
        };
    }
}
