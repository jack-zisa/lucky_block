package dev.creoii.luckyblock.util.position;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.function.Function;

public abstract class PosProvider {
    private static final Codec<Either<Vec3d, PosProvider>> VEC_3D_CODEC = Codec.either(Vec3d.CODEC, LuckyBlockMod.POS_PROVIDER_TYPES.getCodec().dispatch(PosProvider::getType, PosProviderType::codec));
    public static final Codec<PosProvider> VALUE_CODEC = VEC_3D_CODEC.xmap(either -> {
        return either.map(ConstantPosProvider::new, provider -> provider);
    }, provider -> {
        return provider.getType() instanceof ConstantPosProvider constant ? Either.left(constant.getValue()) : Either.right(provider);
    });
    public static Codec<PosProvider> CONSTANT_POS = Codec.either(Codec.DOUBLE, PosProvider.VALUE_CODEC).xmap(either -> {
        return either.map(aDouble -> new ConstantPosProvider(new Vec3d(aDouble, aDouble, aDouble)), Function.identity());
    }, Either::right);

    public abstract Vec3d getVec(Outcome.Context context);

    public BlockPos getPos(Outcome.Context context) {
        return fromVec(getVec(context));
    }

    public abstract PosProviderType<?> getType();

    public static BlockPos fromVec(Vec3d vec3d) {
        return new BlockPos((int) Math.round(vec3d.x), (int) Math.round(vec3d.y), (int) Math.round(vec3d.z));
    }
}
