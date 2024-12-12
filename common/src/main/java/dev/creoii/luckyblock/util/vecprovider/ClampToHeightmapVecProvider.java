package dev.creoii.luckyblock.util.vecprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

import java.util.List;
import java.util.Optional;

public class ClampToHeightmapVecProvider extends VecProvider {
    public static final MapCodec<ClampToHeightmapVecProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(VecProvider.VALUE_CODEC.optionalFieldOf("pos").forGetter(provider -> provider.pos),
                Heightmap.Type.CODEC.fieldOf("heightmap").forGetter(provider -> provider.heightmap)
        ).apply(instance, ClampToHeightmapVecProvider::new);
    });
    private final Optional<VecProvider> pos;
    private final Heightmap.Type heightmap;

    private ClampToHeightmapVecProvider(Optional<VecProvider> pos, Heightmap.Type heightmap) {
        this.pos = pos;
        this.heightmap = heightmap;
    }

    @Override
    public Vec3d getVec(Outcome.Context<?> context) {
        return getPos(context).toCenterPos();
    }

    @Override
    public BlockPos getPos(Outcome.Context<?> context) {
        BlockPos pos = this.pos.isPresent() ? this.pos.get().getPos(context) : context.pos();
        return context.world().getTopPosition(heightmap, pos);
    }

    @Override
    public List<Vec3d> getVecs(Outcome.Context<?> context) {
        return getPositions(context).stream().map(BlockPos::toCenterPos).toList();
    }

    @Override
    public List<BlockPos> getPositions(Outcome.Context<?> context) {
        return List.of(getPos(context));
    }

    @Override
    public VecProviderType<?> getType() {
        return VecProviderType.CLAMP_TO_HEIGHTMAP;
    }
}
