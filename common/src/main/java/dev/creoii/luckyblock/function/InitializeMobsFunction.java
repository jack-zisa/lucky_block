package dev.creoii.luckyblock.function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.target.FunctionTarget;
import dev.creoii.luckyblock.function.target.HasStatusEffectsFunctionTarget;
import dev.creoii.luckyblock.function.target.Target;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class InitializeMobsFunction extends Function<Target<?>> {
    private static final InitializeMobsFunction DEFAULT = new InitializeMobsFunction(HasStatusEffectsFunctionTarget.INSTANCE);
    private static final MapCodec<InitializeMobsFunction> DEFAULT_CODEC = MapCodec.unit(DEFAULT);
    @SuppressWarnings("unchecked")
    private static final MapCodec<InitializeMobsFunction> BASE_CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(HasStatusEffectsFunctionTarget.INSTANCE).forGetter(Function::getTarget)
        ).apply(instance, functionTarget -> new InitializeMobsFunction((FunctionTarget<Target<?>>) functionTarget));
    });
    public static final MapCodec<InitializeMobsFunction> CODEC = Codec.mapEither(DEFAULT_CODEC, BASE_CODEC).xmap(either -> {
        return either.map(java.util.function.Function.identity(), java.util.function.Function.identity());
    }, Either::right);

    protected InitializeMobsFunction(FunctionTarget<Target<?>> target) {
        super(FunctionType.INITIALIZE_MOBS, Phase.POST, target);
    }

    @Override
    public Outcome.Context<? extends ContextInfo> apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = target.getTargets(outcome, context);
        for (Target<?> target : targets) {
            if (target instanceof EntityWrapper entityWrapper && entityWrapper.getEntity() instanceof MobEntity mob && context.world() instanceof ServerWorld serverWorld) {
                mob.initialize(serverWorld, serverWorld.getLocalDifficulty(context.pos()), SpawnReason.NATURAL, null);
            }
        }
        return context.copyFiltered(targets);
    }
}
