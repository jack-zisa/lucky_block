package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.shape.Shape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.Optional;

public class BlockOutcome extends Outcome {
    public static final MapCodec<BlockOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                BlockStateProvider.TYPE_CODEC.fieldOf("state_provider").forGetter(outcome -> outcome.stateProvider),
                Shape.CODEC.optionalFieldOf("shape").forGetter(outcome -> outcome.shape)
        ).apply(instance, BlockOutcome::new);
    });
    private final BlockStateProvider stateProvider;
    private final Optional<Shape> shape;

    public BlockOutcome(Optional<Integer> delay, Optional<String> pos, BlockStateProvider stateProvider, Optional<Shape> shape) {
        super(OutcomeType.BLOCK, delay, pos);
        this.stateProvider = stateProvider;
        this.shape = shape;
    }

    @Override
    public void run(OutcomeContext context) {
        BlockPos place = getPos(context);
        if (shape.isPresent()) {
            shape.get().getBlockPositions(this, context).forEach(pos -> {
                context.world().setBlockState(place.add(pos), stateProvider.get(context.world().getRandom(), place.add(pos)));
            });
        } else context.world().setBlockState(place, stateProvider.get(context.world().getRandom(), place));
    }
}
