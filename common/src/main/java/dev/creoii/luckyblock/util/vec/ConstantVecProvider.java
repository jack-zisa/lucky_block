package dev.creoii.luckyblock.util.vec;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ConstantVecProvider extends VecProvider {
    public static final ConstantVecProvider ZERO = new ConstantVecProvider(Vec3.ZERO);
    public static final MapCodec<ConstantVecProvider> CODEC = Vec3.CODEC.fieldOf("value").xmap(ConstantVecProvider::new, ConstantVecProvider::getValue);
    private final Vec3 value;

    public ConstantVecProvider(Vec3 value) {
        this.value = value;
    }

    public Vec3 getValue() {
        return value;
    }

    @Override
    public Vec3 getVec(Outcome.Context context) {
        return value;
    }

    @Override
    public List<Vec3> getVecs(Outcome.Context context) {
        return List.of(getVec(context));
    }

    @Override
    public VecProviderType<?> getType() {
        return VecProviderType.CONSTANT;
    }
}
