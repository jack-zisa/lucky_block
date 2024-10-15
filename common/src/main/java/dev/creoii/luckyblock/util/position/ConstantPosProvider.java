package dev.creoii.luckyblock.util.position;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.Vec3d;

public class ConstantPosProvider extends PosProvider {
    public static final ConstantPosProvider ZERO = new ConstantPosProvider(Vec3d.ZERO);
    public static final MapCodec<ConstantPosProvider> CODEC = Vec3d.CODEC.fieldOf("value").xmap(ConstantPosProvider::new, ConstantPosProvider::getValue);
    private final Vec3d value;

    public ConstantPosProvider(Vec3d value) {
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
    public PosProviderType<?> getType() {
        return PosProviderType.CONSTANT;
    }
}
