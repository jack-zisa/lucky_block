package dev.creoii.luckyblock.util.vecprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class WorldVecProvider extends VecProvider {
    public static final MapCodec<WorldVecProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(Codec.STRING.fieldOf("value").forGetter(provider -> provider.value)
        ).apply(instance, WorldVecProvider::new);
    });
    private final String value;

    public WorldVecProvider(String value) {
        this.value = value;
    }

    @Override
    public Vec3d getVec(Outcome.Context<?> context) {
        return switch (value) {
            case "spawn_pos" -> context.world().getSpawnPos().toBottomCenterPos();
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }

    @Override
    public List<Vec3d> getVecs(Outcome.Context<?> context) {
        return List.of(getVec(context));
    }

    @Override
    public VecProviderType<?> getType() {
        return VecProviderType.WORLD;
    }
}
