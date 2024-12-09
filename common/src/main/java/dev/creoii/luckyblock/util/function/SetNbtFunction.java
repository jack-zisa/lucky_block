package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.function.target.NbtTarget;
import dev.creoii.luckyblock.util.function.target.NoneFunctionTarget;
import dev.creoii.luckyblock.util.function.target.FunctionTarget;
import dev.creoii.luckyblock.util.function.target.Target;
import net.minecraft.nbt.NbtElement;

public class SetNbtFunction extends Function<Target<?>> {
    @SuppressWarnings("unchecked")
    public static final MapCodec<SetNbtFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FunctionTarget.CODEC.fieldOf("target").orElse(NoneFunctionTarget.INSTANCE).forGetter(Function::getTarget),
                LuckyBlockCodecs.NBT_ELEMENT_CODEC.fieldOf("nbt").forGetter(function -> function.nbt)
        ).apply(instance, (functionTarget, nbtElement) -> new SetNbtFunction((FunctionTarget<Target<?>>) functionTarget, nbtElement));
    });
    private final NbtElement nbt;

    protected SetNbtFunction(FunctionTarget<Target<?>> target, NbtElement nbt) {
        super(FunctionType.SET_NBT, target);
        this.nbt = nbt;
    }

    @Override
    public void apply(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        for (Target<?> target : target.getTargets(outcome, context)) {
            if (target instanceof NbtTarget<?> nbtTarget) {
                nbtTarget.setNbt(nbt);
            }
        }
    }
}
