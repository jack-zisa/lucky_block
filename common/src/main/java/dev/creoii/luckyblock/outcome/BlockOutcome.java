package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.shape.Shape;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
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
                createGlobalReinitField(Outcome::shouldReinit),
                BlockStateProvider.TYPE_CODEC.fieldOf("state_provider").forGetter(outcome -> outcome.stateProvider),
                NbtCompound.CODEC.optionalFieldOf("block_entity").forGetter(outcome -> outcome.blockEntityNbt),
                Shape.CODEC.optionalFieldOf("shape").forGetter(outcome -> outcome.shape)
        ).apply(instance, BlockOutcome::new);
    });
    private final BlockStateProvider stateProvider;
    private final Optional<NbtCompound> blockEntityNbt;
    private final Optional<Shape> shape;

    public BlockOutcome(int luck, float chance, Optional<Integer> delay, Optional<String> pos, boolean reinit, BlockStateProvider stateProvider, Optional<NbtCompound> blockEntityNbt, Optional<Shape> shape) {
        super(OutcomeType.BLOCK, luck, chance, delay, pos, reinit);
        this.stateProvider = stateProvider;
        this.blockEntityNbt = blockEntityNbt;
        this.shape = shape;
    }

    @Override
    public void run(OutcomeContext context) {
        MutableObject<BlockPos> place = new MutableObject<>(getPos(context));
        if (shape.isPresent()) {
            shape.get().getBlockPositions(this, context).forEach(pos -> {
                BlockState state = stateProvider.get(context.world().getRandom(), place.getValue().add(pos));
                context.world().setBlockState(place.getValue().add(pos), state);
                blockEntityNbt.ifPresent(nbtCompound -> context.world().addBlockEntity(BlockEntity.createFromNbt(place.getValue().add(pos), state, nbtCompound, context.world().getRegistryManager())));
                if (shouldReinit()) {
                    place.setValue(getPos(context));
                }
            });
        } else {
            BlockState state = stateProvider.get(context.world().getRandom(), place.getValue());
            context.world().setBlockState(place.getValue(), state);
            blockEntityNbt.ifPresent(nbtCompound -> context.world().addBlockEntity(BlockEntity.createFromNbt(place.getValue(), state, nbtCompound, context.world().getRegistryManager())));
        }
    }
}
