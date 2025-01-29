package dev.creoii.luckyblock.function.target;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.util.entityprovider.EntityProvider;

import java.util.List;

public class IsEntityFunctionTarget extends FunctionTarget<Target<?>> {
    public static final IsEntityFunctionTarget DEFAULT = new IsEntityFunctionTarget(null);
    private static final MapCodec<IsEntityFunctionTarget> DEFAULT_CODEC = MapCodec.unit(DEFAULT);
    private static final MapCodec<IsEntityFunctionTarget> BASE_CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(EntityProvider.CODEC.optionalFieldOf("provider", null).forGetter(functionTarget -> functionTarget.provider)
        ).apply(instance, IsEntityFunctionTarget::new);
    });
    public static final MapCodec<IsEntityFunctionTarget> CODEC = Codec.mapEither(DEFAULT_CODEC, BASE_CODEC).xmap(either -> {
        return either.map(java.util.function.Function.identity(), java.util.function.Function.identity());
    }, Either::right);
    private final EntityProvider provider;

    public IsEntityFunctionTarget(EntityProvider provider) {
        super(FunctionTargetType.IS_ENTITY);
        this.provider = provider;
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = getEntityTargets(context.info());
        if (provider == null)
            return targets;
        else {
            EntityWrapper wrapper = provider.getEntities(context, context.random());
            if (wrapper == null)
                return List.of();
            return List.of(wrapper);
        }
    }
}
