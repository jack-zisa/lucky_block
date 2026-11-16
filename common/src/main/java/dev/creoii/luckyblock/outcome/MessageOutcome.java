package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.ContextualProvider;
import dev.creoii.luckyblock.util.FunctionUtils;
import dev.creoii.luckyblock.util.provider.booleanprovider.BooleanProvider;
import dev.creoii.luckyblock.util.provider.booleanprovider.FalseBooleanProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.valueproviders.IntProvider;

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
    private final Component message;
    private final BooleanProvider overlay;

    public MessageOutcome(int luck, float chance, IntProvider weightProvider, IntProvider delay, Component message, BooleanProvider overlay) {
        super(OutcomeType.MESSAGE, luck, chance, weightProvider, delay, Optional.empty(), false);
        this.message = message;
        this.overlay = overlay;
    }

    @Override
    public void run(Context context) {
        context.world().players().forEach(player -> {
            if (player.level() == context.world()) {
                if (message.getString() == null)
                    return;

                String parsed = FunctionUtils.parseString(message.getString(), context);

                if (overlay.get(player.getRandom())) {
                    player.sendOverlayMessage(MutableComponent.create(Component.literal(parsed)).setStyle(message.getStyle()), ContextualProvider.applyBooleanContext(overlay, context).get(context.world().getRandom()));
                } else {
                    player.sendSystemMessage(MutableComponent.create(Component.literal(parsed)).setStyle(message.getStyle()), ContextualProvider.applyBooleanContext(overlay, context).get(context.world().getRandom()));
                }
            }
        });
    }
}
