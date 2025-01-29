package dev.creoii.luckyblock.util.vecprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.entityprovider.EntityProvider;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class EntityVecProvider extends VecProvider {
    public static final MapCodec<EntityVecProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(EntityProvider.CODEC.fieldOf("entity").forGetter(provider -> provider.entity),
                Codec.STRING.fieldOf("value").forGetter(provider -> provider.value)
        ).apply(instance, EntityVecProvider::new);
    });
    private final EntityProvider entity;
    private final String value;

    public EntityVecProvider(EntityProvider entity, String value) {
        this.entity = entity;
        this.value = value;
    }

    @Override
    public Vec3d getVec(Outcome.Context<?> context) {
        EntityWrapper wrapper = entity.getEntities(context, context.random());
        if (wrapper == null) {
            return Vec3d.ZERO;
        }

        if (wrapper.getEntity() == null) {
            wrapper = wrapper.init(context);
        }

        return switch (value) {
            case "pos" -> wrapper.getEntity().getPos();
            case "eye_pos" -> wrapper.getEntity().getEyePos();
            case "movement" -> wrapper.getEntity().getMovement();
            case "rotation_vector" -> wrapper.getEntity().getRotationVector();
            case "velocity" -> wrapper.getEntity().getVelocity();
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }

    @Override
    public List<Vec3d> getVecs(Outcome.Context<?> context) {
        return List.of(getVec(context));
    }

    @Override
    public VecProviderType<?> getType() {
        return VecProviderType.ENTITY;
    }
}
