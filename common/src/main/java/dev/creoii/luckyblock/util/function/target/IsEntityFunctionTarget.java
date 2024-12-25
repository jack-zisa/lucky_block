package dev.creoii.luckyblock.util.function.target;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.wrapper.EntityWrapper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.TameableEntity;

import java.util.List;
import java.util.stream.Collectors;

public class IsEntityFunctionTarget extends FunctionTarget<Target<?>> {
    public static final IsEntityFunctionTarget DEFAULT = new IsEntityFunctionTarget("");
    private static final MapCodec<IsEntityFunctionTarget> DEFAULT_CODEC = MapCodec.unit(DEFAULT);
    private static final MapCodec<IsEntityFunctionTarget> BASE_CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(Codec.STRING.fieldOf("type_filter").orElse("").forGetter(functionTarget -> functionTarget.typeFilter)
        ).apply(instance, IsEntityFunctionTarget::new);
    });
    public static final MapCodec<IsEntityFunctionTarget> CODEC = Codec.mapEither(DEFAULT_CODEC, BASE_CODEC).xmap(either -> {
        return either.map(java.util.function.Function.identity(), java.util.function.Function.identity());
    }, Either::right);
    private final String typeFilter;

    public IsEntityFunctionTarget(String typeFilter) {
        super(FunctionTargetType.IS_ENTITY);
        this.typeFilter = typeFilter;
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = getEntityTargets(context.info());
        if (typeFilter.isEmpty())
            return targets;
        else {
            return switch (typeFilter) {
                case "living" -> targets.stream().filter(target -> target instanceof EntityWrapper wrapper && !(wrapper.getEntity() instanceof LivingEntity)).collect(Collectors.toList());
                case "mob" -> targets.stream().filter(target -> target instanceof EntityWrapper wrapper && !(wrapper.getEntity() instanceof MobEntity)).collect(Collectors.toList());
                case "pet" -> targets.stream().filter(target -> target instanceof EntityWrapper wrapper && !(wrapper.getEntity() instanceof TameableEntity)).collect(Collectors.toList());
                case "monster" -> targets.stream().filter(target -> target instanceof EntityWrapper wrapper && !(wrapper.getEntity() instanceof Monster)).collect(Collectors.toList());
                default -> throw new IllegalStateException("Unexpected value: " + typeFilter);
            };
        }
    }
}
