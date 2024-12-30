package dev.creoii.luckyblock.function.target;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class HasEquipmentFunctionTarget extends FunctionTarget<Target<?>> {
    public static final HasEquipmentFunctionTarget INSTANCE = new HasEquipmentFunctionTarget();
    public static final MapCodec<HasEquipmentFunctionTarget> CODEC = MapCodec.unit(INSTANCE);

    public HasEquipmentFunctionTarget() {
        super(FunctionTargetType.HAS_EQUIPMENT);
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = Lists.newArrayList();
        for (Object o : context.info().getTargets()) {
            if (o instanceof EquipmentTarget<?> equipmentTarget)
                targets.add(equipmentTarget);
        }
        return targets;
    }
}
