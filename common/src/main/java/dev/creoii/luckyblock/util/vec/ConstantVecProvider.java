package dev.creoii.luckyblock.util.vec;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ConstantVecProvider extends VecProvider {
    public static final ConstantVecProvider ZERO = new ConstantVecProvider(Vec3d.ZERO);
    public static final MapCodec<ConstantVecProvider> CODEC = Vec3d.CODEC.fieldOf("value").xmap(ConstantVecProvider::new, ConstantVecProvider::getValue);
    private final Vec3d value;

    public ConstantVecProvider(Vec3d value) {
        this.value = value;
    }

    public Vec3d getValue() {
        return value;
    }

    @Override
    public Vec3d getVec(Outcome.Context context) {
        return value;
    }

    @Override
    public List<Vec3d> getVecs(Outcome.Context context) {
        return List.of(getVec(context));
    }

    @Override
    public VecProviderType<?> getType() {
        return VecProviderType.CONSTANT;
    }
}
