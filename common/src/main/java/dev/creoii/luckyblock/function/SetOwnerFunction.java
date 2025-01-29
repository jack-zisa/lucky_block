package dev.creoii.luckyblock.function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.target.FunctionTarget;
import dev.creoii.luckyblock.function.target.IsEntityFunctionTarget;
import dev.creoii.luckyblock.function.target.Target;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.ProjectileEntity;

import java.util.List;

public class SetOwnerFunction extends Function<Target<?>> {
    private static final SetOwnerFunction DEFAULT = new SetOwnerFunction(IsEntityFunctionTarget.DEFAULT);
    private static final MapCodec<SetOwnerFunction> DEFAULT_CODEC = MapCodec.unit(DEFAULT);
    @SuppressWarnings("unchecked")
    private static final MapCodec<SetOwnerFunction> BASE_CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(IsEntityFunctionTarget.DEFAULT).forGetter(Function::getTarget)
        ).apply(instance, functionTarget -> new SetOwnerFunction((FunctionTarget<Target<?>>) functionTarget));
    });
    public static final MapCodec<SetOwnerFunction> CODEC = Codec.mapEither(DEFAULT_CODEC, BASE_CODEC).xmap(either -> {
        return either.map(java.util.function.Function.identity(), java.util.function.Function.identity());
    }, Either::right);

    protected SetOwnerFunction(FunctionTarget<Target<?>> target) {
        super(FunctionType.SET_OWNER, Phase.POST, target);
    }

    @Override
    public Outcome.Context<? extends ContextInfo> apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        if (context.source() == null)
            return context;

        List<Target<?>> targets = target.getTargets(outcome, context);
        for (Target<?> target : targets) {
            if (target instanceof EntityWrapper wrapper) {
                if (wrapper.getEntity() instanceof TameableEntity tameable) {
                    tameable.setOwner(context.source());
                } else if (wrapper.getEntity() instanceof ProjectileEntity projectile) {
                    projectile.setOwner(context.source());
                }
            }
        }
        return context.copyFiltered(targets);
    }
}
