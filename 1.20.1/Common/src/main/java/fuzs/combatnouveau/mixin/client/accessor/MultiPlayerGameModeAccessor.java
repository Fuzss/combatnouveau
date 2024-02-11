package fuzs.combatnouveau.mixin.client.accessor;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultiPlayerGameMode.class)
public interface MultiPlayerGameModeAccessor {

    @Invoker("ensureHasSentCarriedItem")
    void combatnouveau$callEnsureHasSentCarriedItem();

    @Invoker("startPrediction")
    void combatnouveau$callStartPrediction(ClientLevel clientLevel, PredictiveAction predictiveAction);
}
