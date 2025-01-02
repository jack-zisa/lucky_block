package dev.creoii.luckyblock.function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.target.FunctionTarget;
import dev.creoii.luckyblock.function.target.IsEntityFunctionTarget;
import dev.creoii.luckyblock.function.target.Target;
import dev.creoii.luckyblock.function.target.VariantTarget;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;

public class SetVariantFunction extends Function<Target<?>> {
    @SuppressWarnings("unchecked")
    public static final MapCodec<SetVariantFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(IsEntityFunctionTarget.DEFAULT).forGetter(Function::getTarget),
                Codec.STRING.fieldOf("variant").forGetter(function -> function.variant.right().orElse(""))
        ).apply(instance, (functionTarget, variant) -> new SetVariantFunction((FunctionTarget<Target<?>>) functionTarget, Either.right(variant)));
    });
    private final Either<Integer, String> variant;

    protected SetVariantFunction(FunctionTarget<Target<?>> target, Either<Integer, String> variant) {
        super(FunctionType.SET_VARIANT, Phase.POST, target);
        this.variant = variant;
    }

    @Override
    public void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        for (Target<?> target : target.getTargets(outcome, context)) {
            if (target instanceof VariantTarget<?> variantTarget) {
                variantTarget.setVariant(outcome, context, variant);
            }
        }
    }
}
