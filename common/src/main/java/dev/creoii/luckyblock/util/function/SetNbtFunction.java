package dev.creoii.luckyblock.util.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.BlockOutcome;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import net.minecraft.nbt.NbtElement;

public class SetNbtFunction extends Function {
    public static final MapCodec<SetNbtFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(LuckyBlockCodecs.NBT_ELEMENT_CODEC.fieldOf("nbt").forGetter(function -> function.nbt)
        ).apply(instance, SetNbtFunction::new);
    });
    private final NbtElement nbt;

    protected SetNbtFunction(NbtElement nbt) {
        super(FunctionType.SET_NBT);
        this.nbt = nbt;
    }

    /**
     * TODO: Context should have extra information, like an entity, to apply NBT to
     */
    public <T extends ContextInfo> void apply(Outcome<T> outcome, Outcome.Context<T> context) {
        if (context.info() instanceof BlockOutcome.BlockInfo blockInfo) {

        }
    }
}
