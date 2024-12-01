package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.nbt.ContextualNbtCompound;
import dev.creoii.luckyblock.util.vec.VecProvider;
import dev.creoii.luckyblock.util.shape.Shape;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Optional;

public class BlockOutcome extends Outcome {
    public static final MapCodec<BlockOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                BlockStateProvider.TYPE_CODEC.fieldOf("state_provider").forGetter(outcome -> outcome.stateProvider),
                ContextualNbtCompound.CODEC.optionalFieldOf("block_entity").forGetter(outcome -> outcome.blockEntity),
                Shape.CODEC.optionalFieldOf("shape").forGetter(outcome -> outcome.shape)
        ).apply(instance, BlockOutcome::new);
    });
    private final BlockStateProvider stateProvider;
    private final Optional<ContextualNbtCompound> blockEntity;
    private final Optional<Shape> shape;

    public BlockOutcome(int luck, float chance, int delay, Optional<VecProvider> pos, BlockStateProvider stateProvider, Optional<ContextualNbtCompound> blockEntity, Optional<Shape> shape) {
        super(OutcomeType.BLOCK, luck, chance, delay, pos, false);
        this.stateProvider = stateProvider;
        this.blockEntity = blockEntity;
        this.shape = shape;
    }

    @Override
    public void run(Context context) {
        MutableObject<BlockPos> place = new MutableObject<>(getPos(context).getPos(context));
        if (shape.isPresent()) {
            shape.get().getBlockPositions(context).forEach(pos -> {
                BlockState state = stateProvider.get(context.world().getRandom(), place.getValue().add(pos));
                if (context.world().setBlockState(place.getValue().add(pos), state)) {
                    blockEntity.ifPresent(nbtCompound -> {
                        nbtCompound.setContext(context);
                        context.world().addBlockEntity(BlockEntity.createFromNbt(place.getValue().add(pos), state, nbtCompound, context.world().getRegistryManager()));
                    });
                }
            });
        } else {
            BlockState state = stateProvider.get(context.world().getRandom(), place.getValue());
            if (context.world().setBlockState(place.getValue(), state)) {
                blockEntity.ifPresent(nbtCompound -> {
                    nbtCompound.setContext(context);
                    context.world().addBlockEntity(BlockEntity.createFromNbt(place.getValue(), state, nbtCompound, context.world().getRegistryManager()));
                });
            }
        }
    }
}
