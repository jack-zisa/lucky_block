package dev.creoii.luckyblock.util.vec;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

import java.util.List;
import java.util.Optional;

public class HeightmapVecProvider extends VecProvider {
    public static final MapCodec<HeightmapVecProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(VecProvider.VALUE_CODEC.optionalFieldOf("center").forGetter(provider -> provider.center),
                Heightmap.Type.CODEC.fieldOf("heightmap").forGetter(provider -> provider.heightmap)
        ).apply(instance, HeightmapVecProvider::new);
    });
    private final Optional<VecProvider> center;
    private final Heightmap.Type heightmap;

    private HeightmapVecProvider(Optional<VecProvider> center, Heightmap.Type heightmap) {
        this.center = center;
        this.heightmap = heightmap;
    }

    @Override
    public Vec3d getVec(Outcome.Context context) {
        return getPos(context).toCenterPos();
    }

    @Override
    public BlockPos getPos(Outcome.Context context) {
        BlockPos center = this.center.isPresent() ? this.center.get().getPos(context) : context.pos();
        return context.world().getTopPosition(heightmap, center);
    }

    @Override
    public List<Vec3d> getVecs(Outcome.Context context) {
        return getPositions(context).stream().map(BlockPos::toCenterPos).toList();
    }

    @Override
    public List<BlockPos> getPositions(Outcome.Context context) {
        return List.of(getPos(context));
    }

    @Override
    public VecProviderType<?> getType() {
        return VecProviderType.HEIGHTMAP;
    }
}
