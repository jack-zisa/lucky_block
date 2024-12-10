package dev.creoii.luckyblock.util.vec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;

import java.util.List;
import java.util.function.Function;

public class RandomVecProvider extends VecProvider {
    public static final RandomVecProvider ZERO = new RandomVecProvider(LuckyBlockCodecs.ONE_F, LuckyBlockCodecs.ONE_F, LuckyBlockCodecs.ONE_F);
    public static final RandomVecProvider DEFAULT_ITEM_VELOCITY = new RandomVecProvider(UniformFloatProvider.create(-.1f, .1f), ConstantFloatProvider.create(.2f), UniformFloatProvider.create(-.1f, .1f));
    public static Codec<List<FloatProvider>> BASE_FLOAT_PROVIDER_CODEC = Codec.either(Vec3d.CODEC, FloatProvider.VALUE_CODEC.listOf()).xmap(either -> {
        return either.map(vec3d -> List.of(ConstantFloatProvider.create((float) vec3d.x), ConstantFloatProvider.create((float) vec3d.y), ConstantFloatProvider.create((float) vec3d.z)), Function.identity());
    }, Either::right);
    public static MapCodec<RandomVecProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FloatProvider.VALUE_CODEC.fieldOf("x").forGetter(provider -> provider.x),
                FloatProvider.VALUE_CODEC.fieldOf("y").forGetter(provider -> provider.y),
                FloatProvider.VALUE_CODEC.fieldOf("z").forGetter(provider -> provider.z)
        ).apply(instance, RandomVecProvider::new);
    });
    private final FloatProvider x;
    private final FloatProvider y;
    private final FloatProvider z;

    public RandomVecProvider(FloatProvider x, FloatProvider y, FloatProvider z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Vec3d getVec(Outcome.Context context) {
        return new Vec3d(x.get(context.world().getRandom()), y.get(context.world().getRandom()), z.get(context.world().getRandom()));
    }

    @Override
    public List<Vec3d> getVecs(Outcome.Context context) {
        return List.of(getVec(context));
    }

    @Override
    public VecProviderType<?> getType() {
        return VecProviderType.RANDOM;
    }
}
