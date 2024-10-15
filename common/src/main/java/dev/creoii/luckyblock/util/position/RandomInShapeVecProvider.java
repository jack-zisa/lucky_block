package dev.creoii.luckyblock.util.position;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.shape.Shape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class RandomInShapeVecProvider extends VecProvider {
    public static final MapCodec<RandomInShapeVecProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(Vec3d.CODEC.fieldOf("center").forGetter(provider -> provider.center),
                Shape.CODEC.fieldOf("shape").forGetter(provider -> provider.shape)
        ).apply(instance, RandomInShapeVecProvider::new);
    });
    private final Vec3d center;
    private final Shape shape;

    private RandomInShapeVecProvider(Vec3d center, Shape shape) {
        this.center = center;
        this.shape = shape;
    }

    @Override
    public Vec3d getVec(Outcome.Context context) {
        return getPos(context).toCenterPos();
    }

    @Override
    public BlockPos getPos(Outcome.Context context) {
        List<BlockPos> positions = shape.getBlockPositions(null, context);
        if (positions.isEmpty()) {
            return ConstantVecProvider.ZERO.getPos(context);
        }
        return positions.get(context.world().getRandom().nextInt(positions.size())).add((int) Math.round(center.x), (int) Math.round(center.y), (int) Math.round(center.z));
    }

    @Override
    public PosProviderType<?> getType() {
        return PosProviderType.RANDOM_IN_SHAPE;
    }
}
