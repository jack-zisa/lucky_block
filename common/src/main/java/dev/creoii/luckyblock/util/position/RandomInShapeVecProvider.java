package dev.creoii.luckyblock.util.position;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.shape.Shape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Optional;

public class RandomInShapeVecProvider extends VecProvider {
    public static final MapCodec<RandomInShapeVecProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(VecProvider.VALUE_CODEC.optionalFieldOf("center").forGetter(provider -> provider.center),
                Shape.CODEC.fieldOf("shape").forGetter(provider -> provider.shape)
        ).apply(instance, RandomInShapeVecProvider::new);
    });
    private final Optional<VecProvider> center;
    private final Shape shape;

    private RandomInShapeVecProvider(Optional<VecProvider> center, Shape shape) {
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
        Vec3d center = this.center.isPresent() ? this.center.get().getVec(context) : context.pos().toCenterPos();
        return positions.get(context.world().getRandom().nextInt(positions.size())).add((int) Math.round(center.x), (int) Math.round(center.y), (int) Math.round(center.z));
    }

    @Override
    public VecProviderType<?> getType() {
        return VecProviderType.RANDOM_IN_SHAPE;
    }
}
