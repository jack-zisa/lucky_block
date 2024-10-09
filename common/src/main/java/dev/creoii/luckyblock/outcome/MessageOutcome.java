package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.Optional;

public class MessageOutcome extends Outcome {
    public static final MapCodec<MessageOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalDelayField(Outcome::getDelay),
                TextCodecs.CODEC.fieldOf("message").forGetter(outcome -> outcome.message)
        ).apply(instance, MessageOutcome::new);
    });
    private final Text message;

    public MessageOutcome(Optional<Integer> delay, Text message) {
        super(OutcomeType.MESSAGE, delay, Optional.empty());
        this.message = message;
    }

    @Override
    public void run(OutcomeContext context) {
        context.player().sendMessage(context.processText(message), false);
    }
}
