package dev.creoii.luckyblock.util.position;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.List;

public class RandomVelocityVecProvider extends VecProvider {
    public static final MapCodec<RandomVelocityVecProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(FloatProvider.VALUE_CODEC.fieldOf("power").orElse(ConstantFloatProvider.create(.9f)).forGetter(provider -> provider.power),
                IntProvider.createValidatingCodec(0, 90).fieldOf("pitch").orElse(ConstantIntProvider.create(15)).forGetter(provider -> provider.pitch)
        ).apply(instance, RandomVelocityVecProvider::new);
    });
    private final FloatProvider power;
    private final IntProvider pitch;

    private RandomVelocityVecProvider(FloatProvider power, IntProvider pitch) {
        this.power = power;
        this.pitch = pitch;
    }

    @Override
    public Vec3d getVec(Outcome.Context context) {
        float power = this.power.get(context.world().getRandom());
        int pitch = this.pitch.get(context.world().getRandom());

        float yawRad = (float) Math.toRadians(context.world().getRandom().nextBetween(-180, 180));
        float pitchRad = (float) Math.toRadians(-90d + context.world().getRandom().nextBetween(-pitch, pitch));
        return new Vec3d(-MathHelper.sin(yawRad) * MathHelper.cos(pitchRad) * power, -MathHelper.sin(pitchRad) * power, MathHelper.cos(yawRad) * MathHelper.cos(pitchRad) * power);
    }

    @Override
    public List<Vec3d> getVecs(Outcome.Context context) {
        return List.of(getVec(context));
    }

    @Override
    public VecProviderType<?> getType() {
        return VecProviderType.RANDOM_VELOCITY;
    }
}
