package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.nbt.ContextualNbtCompound;
import dev.creoii.luckyblock.util.vec.VecProvider;
import dev.creoii.luckyblock.util.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.Optional;

public class BlockOutcome extends Outcome {
    public static final MapCodec<BlockOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(outcome -> outcome.delay),
                createGlobalPosField(Outcome::getPos),
                BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(outcome -> outcome.stateProvider),
                ContextualNbtCompound.CODEC.optionalFieldOf("block_entity").forGetter(outcome -> outcome.blockEntity),
                Shape.CODEC.optionalFieldOf("shape").forGetter(outcome -> outcome.shape)
        ).apply(instance, BlockOutcome::new);
    });
    private final Holder<BlockStateProvider> stateProvider;
    private final Optional<ContextualNbtCompound> blockEntity;
    private final Optional<Shape> shape;

    public BlockOutcome(int luck, float chance, IntProvider weightProvider, IntProvider delay, Optional<VecProvider> pos, Holder<BlockStateProvider> stateProvider, Optional<ContextualNbtCompound> blockEntity, Optional<Shape> shape) {
        super(OutcomeType.BLOCK, luck, chance, weightProvider, delay, pos, false);
        this.stateProvider = stateProvider;
        this.blockEntity = blockEntity;
        this.shape = shape;
    }

    @Override
    public void run(Context context) {
        BlockPos.MutableBlockPos place = getPos(context).getPos(context).mutable();
        if (shape.isPresent()) {
            shape.get().getBlockPositions(context).forEach(pos -> {
                BlockState state = stateProvider.value().getState(context.world(), context.world().getRandom(), place.offset(pos));
                if (context.world().setBlockAndUpdate(place.offset(pos), state)) {
                    blockEntity.ifPresent(nbtCompound -> {
                        nbtCompound.setContext(context);
                        context.world().setBlockEntity(BlockEntity.loadStatic(place.offset(pos), state, nbtCompound, context.world().registryAccess()));
                    });
                }
            });
        } else {
            BlockState state = stateProvider.value().getState(context.world(), context.world().getRandom(), place);
            if (context.world().setBlockAndUpdate(place, state)) {
                blockEntity.ifPresent(nbtCompound -> {
                    nbtCompound.setContext(context);
                    context.world().setBlockEntity(BlockEntity.loadStatic(place, state, nbtCompound, context.world().registryAccess()));
                });
            }
        }
    }
}
