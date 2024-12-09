package dev.creoii.luckyblock.util.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class NbtFunctionTarget extends FunctionTarget<Target<?>> {
    public static final NbtFunctionTarget INSTANCE = new NbtFunctionTarget();
    /**
     * Add options for block entities, entities, nbt item component types
     */
    public static final MapCodec<NbtFunctionTarget> CODEC = MapCodec.unit(INSTANCE);

    public NbtFunctionTarget() {
        super(FunctionTargetType.NBT);
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = new ArrayList<>();
        for (Object o : context.info().getTargets()) {
            if (o instanceof BlockEntity blockEntity) {
                targets.add(new FunctionTarget.BlockEntityTarget(blockEntity));
            } else if (o instanceof Entity entity) {
                targets.add(new FunctionTarget.EntityTarget(entity));
            }
        }
        return targets;
    }
}
