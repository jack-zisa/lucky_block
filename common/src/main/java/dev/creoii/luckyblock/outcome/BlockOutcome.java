package dev.creoii.luckyblock.outcome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.nbt.ContextualNbtCompound;
import dev.creoii.luckyblock.util.vecprovider.VecProvider;
import dev.creoii.luckyblock.util.shape.Shape;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BlockOutcome extends Outcome<BlockOutcome.BlockInfo> {
    public static final MapCodec<BlockOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                BlockStateProvider.TYPE_CODEC.fieldOf("state").forGetter(outcome -> outcome.stateProvider),
                ContextualNbtCompound.CODEC.fieldOf("block_entity").orElse(null).forGetter(outcome -> outcome.blockEntityNbt),
                Shape.CODEC.fieldOf("shape").orElse(null).forGetter(outcome -> outcome.shape)
        ).apply(instance, BlockOutcome::new);
    });
    private final BlockStateProvider stateProvider;
    private final ContextualNbtCompound blockEntityNbt;
    private final Shape shape;

    public BlockOutcome(int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, BlockStateProvider stateProvider, ContextualNbtCompound blockEntityNbt, Shape shape) {
        super(OutcomeType.BLOCK, luck, chance, weightProvider, delay, pos, false);
        this.stateProvider = stateProvider;
        this.blockEntityNbt = blockEntityNbt;
        this.shape = shape;
    }

    @Override
    public Context<BlockInfo> create(Context<BlockInfo> context) {
        BlockPos pos = getPos(context).getPos(context);

        BlockInfo blockInfo;
        Map<BlockPos, Pair<BlockState, BlockEntity>> blocks = Maps.newHashMap();
        if (shape != null) {
            shape.getBlockPositions(context).forEach(pos1 -> {
                BlockState state = stateProvider.get(context.world().getRandom(), pos.add(pos1));
                if (blockEntityNbt != null) {
                    blockEntityNbt.setContext(context);
                    BlockEntity blockEntity = BlockEntity.createFromNbt(pos.add(pos1), state, blockEntityNbt, context.world().getRegistryManager());
                    blocks.put(pos, new Pair<>(state, blockEntity));
                } else {
                    blocks.put(pos, new Pair<>(state, null));
                }
            });
            blockInfo = new BlockInfo(pos, blocks);
        } else {
            BlockState state = stateProvider.get(context.world().getRandom(), pos);
            if (blockEntityNbt != null) {
                blockEntityNbt.setContext(context);
                BlockEntity blockEntity = BlockEntity.createFromNbt(pos, state, blockEntityNbt, context.world().getRegistryManager());
                blocks.put(pos, new Pair<>(state, blockEntity));
            } else {
                blocks.put(pos, new Pair<>(state, null));
            }
            blockInfo = new BlockInfo(pos, blocks);
        }
        return context.withInfo(blockInfo);
    }

    @Override
    public void run(Context<BlockInfo> context) {
        for (Map.Entry<BlockPos, Pair<BlockState, BlockEntity>> blocks : context.info().blocks.entrySet()) {
            Pair<BlockState, BlockEntity> state = blocks.getValue();
            if (context.world().setBlockState(blocks.getKey(), state.getLeft()) && state.getRight() != null) {
                context.world().addBlockEntity(state.getRight());
            }
        }
    }

    public class BlockInfo implements ContextInfo {
        private final BlockPos pos;
        private final Map<BlockPos, Pair<BlockState, BlockEntity>> blocks;

        public BlockInfo(BlockPos pos, Map<BlockPos, Pair<BlockState, BlockEntity>> blocks) {
            this.pos = pos;
            this.blocks = blocks;
        }

        public BlockPos getPos() {
            return pos;
        }

        @Override
        public List<Object> getTargets() {
            List<Object> targets = Lists.newArrayList(BlockOutcome.this, pos);
            for (Map.Entry<BlockPos, Pair<BlockState, BlockEntity>> entry : blocks.entrySet()) {
                targets.add(entry.getKey());
                Pair<BlockState, BlockEntity> pair = entry.getValue();
                targets.add(pair.getLeft());
                if (pair.getRight() != null) {
                    targets.add(pair.getRight());
                }
            }
            return targets;
        }
    }
}
