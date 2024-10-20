package dev.creoii.luckyblock.util.vec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;

import java.util.List;
import java.util.function.Function;

public abstract class VecProvider {
    private static final Codec<Either<List<FloatProvider>, VecProvider>> VEC_3D_CODEC = Codec.either(RandomVecProvider.BASE_FLOAT_PROVIDER_CODEC, LuckyBlockMod.POS_PROVIDER_TYPES.getCodec().dispatch(VecProvider::getType, VecProviderType::codec));
    public static final Codec<VecProvider> VALUE_CODEC = VEC_3D_CODEC.xmap(either -> {
        return either.map(list -> switch (list.size()) {
            case 0 -> ConstantVecProvider.ZERO;
            case 1 -> new RandomVecProvider(list.getFirst(), ConstantFloatProvider.ZERO, ConstantFloatProvider.ZERO);
            case 2 -> new RandomVecProvider(list.getFirst(), list.get(1), ConstantFloatProvider.ZERO);
            default -> new RandomVecProvider(list.getFirst(), list.get(1), list.get(2));
        }, provider -> provider);
    }, Either::right);
    public static Codec<VecProvider> CONSTANT_POS = Codec.either(Codec.DOUBLE, VecProvider.VALUE_CODEC).xmap(either -> {
        return either.map(aDouble -> new ConstantVecProvider(new Vec3d(aDouble, aDouble, aDouble)), Function.identity());
    }, Either::right);

    public abstract Vec3d getVec(Outcome.Context context);

    public BlockPos getPos(Outcome.Context context) {
        return fromVec(getVec(context));
    }

    public abstract List<Vec3d> getVecs(Outcome.Context context);

    public List<BlockPos> getPositions(Outcome.Context context) {
        return getVecs(context).stream().map(VecProvider::fromVec).toList();
    }

    public abstract VecProviderType<?> getType();

    public static BlockPos fromVec(Vec3d vec3d) {
        return new BlockPos(MathHelper.floor(vec3d.x), MathHelper.floor(vec3d.y), MathHelper.floor(vec3d.z));
    }
}
