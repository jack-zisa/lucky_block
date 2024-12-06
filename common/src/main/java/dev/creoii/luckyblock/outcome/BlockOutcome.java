package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.nbt.ContextualNbtCompound;
import dev.creoii.luckyblock.util.vec.VecProvider;
import dev.creoii.luckyblock.util.shape.Shape;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Map;
import java.util.Optional;

public class BlockOutcome extends Outcome<BlockOutcome.BlockInfo> {
    public static final MapCodec<BlockOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                BlockStateProvider.TYPE_CODEC.fieldOf("state_provider").forGetter(outcome -> outcome.stateProvider),
                ContextualNbtCompound.CODEC.optionalFieldOf("block_entity").forGetter(outcome -> outcome.blockEntityNbt),
                Shape.CODEC.optionalFieldOf("shape").forGetter(outcome -> outcome.shape)
        ).apply(instance, BlockOutcome::new);
    });
    private final BlockStateProvider stateProvider;
    private final Optional<ContextualNbtCompound> blockEntityNbt;
    private final Optional<Shape> shape;

    public BlockOutcome(int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, BlockStateProvider stateProvider, Optional<ContextualNbtCompound> blockEntityNbt, Optional<Shape> shape) {
        super(OutcomeType.BLOCK, luck, chance, weightProvider, delay, pos, false);
        this.stateProvider = stateProvider;
        this.blockEntityNbt = blockEntityNbt;
        this.shape = shape;
    }

    @Override
    public void run(Context<BlockInfo> context) {
        Mutable<BlockPos> place = new MutableObject<>(getPos(context).getPos(context));
        context.info().pos.setValue(place.getValue());
        if (shape.isPresent()) {
            shape.get().getBlockPositions(context).forEach(pos -> {
                BlockState state = stateProvider.get(context.world().getRandom(), place.getValue().add(pos));
                if (context.world().setBlockState(place.getValue().add(pos), state)) {
                    blockEntityNbt.ifPresent(nbtCompound -> {
                        nbtCompound.setContext(context);
                        context.info().pos.setValue(place.getValue().add(pos));
                        context.world().addBlockEntity(BlockEntity.createFromNbt(place.getValue().add(pos), state, nbtCompound, context.world().getRegistryManager()));
                    });
                }
            });
        } else {
            BlockState state = stateProvider.get(context.world().getRandom(), place.getValue());
            if (context.world().setBlockState(place.getValue(), state)) {
                if (blockEntityNbt.isPresent()) {
                    blockEntityNbt.get().setContext(context);
                    BlockEntity blockEntity = BlockEntity.createFromNbt(place.getValue(), state, blockEntityNbt.get(), context.world().getRegistryManager());
                    context.info().blocks.put(place.getValue(), new Pair<>(state, blockEntity));
                    context.world().addBlockEntity(blockEntity);
                } else {
                    context.info().blocks.put(place.getValue(), new Pair<>(state, null));
                }
            }
        }
    }

    public record BlockInfo(Mutable<BlockPos> pos, Map<BlockPos, Pair<BlockState, BlockEntity>> blocks) implements ContextInfo {}
}
