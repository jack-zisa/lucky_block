package dev.creoii.luckyblock.util.vec;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.ContextualProvider;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.shape.Shape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RandomInShapeVecProvider extends VecProvider {
    public static final MapCodec<RandomInShapeVecProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(VecProvider.VALUE_CODEC.optionalFieldOf("center").forGetter(provider -> provider.center),
                Shape.CODEC.fieldOf("shape").forGetter(provider -> provider.shape),
                IntProvider.POSITIVE_CODEC.fieldOf("count").orElse(LuckyBlockCodecs.ONE).forGetter(provider -> provider.count)
        ).apply(instance, RandomInShapeVecProvider::new);
    });
    private final Optional<VecProvider> center;
    private final Shape shape;
    private final IntProvider count;

    private RandomInShapeVecProvider(Optional<VecProvider> center, Shape shape, IntProvider count) {
        this.center = center;
        this.shape = shape;
        this.count = count;
    }

    @Override
    public Vec3d getVec(Outcome.Context context) {
        return getPos(context).toCenterPos();
    }

    @Override
    public BlockPos getPos(Outcome.Context context) {
        List<BlockPos> positions = shape.getBlockPositions(context);
        if (positions.isEmpty()) {
            return ConstantVecProvider.ZERO.getPos(context);
        }
        BlockPos center = this.center.isPresent() ? this.center.get().getPos(context) : context.pos();
        return positions.get(context.world().getRandom().nextInt(positions.size())).add(center);
    }

    @Override
    public List<Vec3d> getVecs(Outcome.Context context) {
        return getPositions(context).stream().map(BlockPos::toCenterPos).toList();
    }

    @Override
    public List<BlockPos> getPositions(Outcome.Context context) {
        List<BlockPos> positions = shape.getBlockPositions(context);
        if (positions.isEmpty()) {
            return List.of(ConstantVecProvider.ZERO.getPos(context));
        }
        BlockPos center = this.center.isPresent() ? this.center.get().getPos(context) : context.pos();

        IntProvider countProvider = this.count;
        if (countProvider instanceof ContextualProvider<?> contextualProvider && contextualProvider.getValueType() == ContextualProvider.Type.INT) {
            countProvider = (IntProvider) contextualProvider.withContext(context);
        }
        int count = countProvider.get(context.world().getRandom());
        if (count > 1) {
            List<BlockPos> result = new ArrayList<>();
            Collections.shuffle(positions);
            for (int i = 0; i < count; ++i) {
                result.add(positions.get(i));
            }
            return result;
        }

        return List.of(positions.get(context.world().getRandom().nextInt(positions.size())).add(center));
    }

    @Override
    public VecProviderType<?> getType() {
        return VecProviderType.RANDOM_IN_SHAPE;
    }
}
