package dev.creoii.luckyblock.util.vec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Function;

public abstract class VecProvider {
    private static final Codec<Either<Vec3d, VecProvider>> VEC_3D_CODEC = Codec.either(Vec3d.CODEC, LuckyBlockMod.POS_PROVIDER_TYPES.getCodec().dispatch(VecProvider::getType, VecProviderType::codec));
    public static final Codec<VecProvider> VALUE_CODEC = VEC_3D_CODEC.xmap(either -> {
        return either.map(ConstantVecProvider::new, provider -> provider);
    }, provider -> {
        return provider.getType() instanceof ConstantVecProvider constant ? Either.left(constant.getValue()) : Either.right(provider);
    });
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
