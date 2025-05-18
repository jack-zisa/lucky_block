package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.ContextualProvider;
import dev.creoii.luckyblock.util.FunctionUtils;
import dev.creoii.luckyblock.util.provider.booleanprovider.BooleanProvider;
import dev.creoii.luckyblock.util.provider.booleanprovider.FalseBooleanProvider;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.Optional;

public class MessageOutcome extends Outcome {
    public static final MapCodec<MessageOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(outcome -> outcome.delay),
                TextCodecs.CODEC.fieldOf("message").forGetter(outcome -> outcome.message),
                BooleanProvider.TYPE_CODEC.fieldOf("overlay").orElse(FalseBooleanProvider.FALSE).forGetter(outcome -> outcome.overlay)
        ).apply(instance, MessageOutcome::new);
    });
    private final Text message;
    private final BooleanProvider overlay;

    public MessageOutcome(int luck, float chance, IntProvider weightProvider, IntProvider delay, Text message, BooleanProvider overlay) {
        super(OutcomeType.MESSAGE, luck, chance, weightProvider, delay, Optional.empty(), false);
        this.message = message;
        this.overlay = overlay;
    }

    @Override
    public void run(Context context) {
        context.world().getPlayers().forEach(player -> {
            if (player.getWorld() == context.world()) {
                if (message.getString() == null)
                    return;

                String parsed = FunctionUtils.parseString(message.getString(), context);
                player.sendMessage(MutableText.of(PlainTextContent.of(parsed)).setStyle(message.getStyle()), ContextualProvider.applyBooleanContext(overlay, context).get(context.world().random));
            }
        });
    }
}
