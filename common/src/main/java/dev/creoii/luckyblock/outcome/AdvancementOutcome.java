package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.ContextualProvider;
import dev.creoii.luckyblock.util.provider.stringprovider.StringProvider;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.Optional;

public class AdvancementOutcome extends Outcome {
    public static final MapCodec<AdvancementOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(outcome -> outcome.delay),
                StringProvider.CODEC.fieldOf("advancement").forGetter(outcome -> outcome.advancement)
        ).apply(instance, AdvancementOutcome::new);
    });
    private final StringProvider advancement;

    public AdvancementOutcome(int luck, float chance, IntProvider weightProvider, IntProvider delay, StringProvider advancement) {
        super(OutcomeType.ADVANCEMENT, luck, chance, weightProvider, delay, Optional.empty(), false);
        this.advancement = advancement;
    }

    @Override
    public void run(Context context) {
        if (context.player() == null)
            return;

        MinecraftServer server = context.world().getServer();
        if (server != null && context.player() instanceof ServerPlayerEntity serverPlayer) {
            AdvancementEntry entry = server.getAdvancementLoader().get(Identifier.tryParse(ContextualProvider.applyStringContext(advancement, context).get(context.world().getRandom())));
            if (entry == null)
                return;

            entry.value().criteria().forEach((s, criterion) -> {
                serverPlayer.getAdvancementTracker().grantCriterion(entry, s);
            });
        }
    }
}
