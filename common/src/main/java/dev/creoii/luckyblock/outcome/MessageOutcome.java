package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.Optional;

public class MessageOutcome extends Outcome {
    public static final MapCodec<MessageOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalDelayField(Outcome::getDelay),
                TextCodecs.CODEC.fieldOf("message").forGetter(outcome -> outcome.message)
        ).apply(instance, MessageOutcome::new);
    });
    private final Text message;

    public MessageOutcome(int luck, float chance, Optional<Integer> delay, Text message) {
        super(OutcomeType.MESSAGE, luck, chance, delay, Optional.empty(), false);
        this.message = message;
    }

    @Override
    public void run(OutcomeContext context) {
        context.world().getPlayers().forEach(player -> {
            if (player.getWorld() == context.world()) {
                player.sendMessage(context.processText(message), false);
            }
        });
    }
}
