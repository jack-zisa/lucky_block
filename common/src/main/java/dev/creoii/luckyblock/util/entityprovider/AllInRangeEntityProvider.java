package dev.creoii.luckyblock.util.entityprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.FunctionContainer;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.vecprovider.VecProvider;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AllInRangeEntityProvider extends EntityProvider {
    public static final MapCodec<AllInRangeEntityProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(VecProvider.VALUE_CODEC.fieldOf("center").forGetter(provider -> provider.center),
                FloatProvider.VALUE_CODEC.fieldOf("dx").forGetter(provider -> provider.dx),
                FloatProvider.VALUE_CODEC.fieldOf("dy").forGetter(provider -> provider.dy),
                FloatProvider.VALUE_CODEC.fieldOf("dz").forGetter(provider -> provider.dz),
                EntityPredicate.CODEC.fieldOf("predicate").forGetter(provider -> provider.predicate),
                FunctionContainer.CODEC.fieldOf("functions").forGetter(provider -> provider.functions)
        ).apply(instance, AllInRangeEntityProvider::new);
    });
    private final VecProvider center;
    private final FloatProvider dx;
    private final FloatProvider dy;
    private final FloatProvider dz;
    private final EntityPredicate predicate;
    private final FunctionContainer functions;

    protected AllInRangeEntityProvider(VecProvider center, FloatProvider dx, FloatProvider dy, FloatProvider dz, EntityPredicate predicate, FunctionContainer functions) {
        super(false);
        this.center = center;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.predicate = predicate;
        this.functions = functions;
    }

    protected EntityProviderType<?> getType() {
        return EntityProviderType.ALL_IN_RANGE_ENTITY_PROVIDER;
    }

    @Override
    public @Nullable List<EntityWrapper> getEntities(Outcome.Context<?> context, Random random) {
        Vec3d vec3d = center.getVec(context);
        if (!(context.world() instanceof ServerWorld))
            return null;

        List<Entity> entities = context.world().getOtherEntities(null, Box.of(vec3d, dx.get(context.random()), dy.get(context.random()), dz.get(context.random())), entity1 -> predicate.test((ServerWorld) context.world(), vec3d, entity1));
        return entities.stream().map(entity -> new EntityWrapper(entity, functions)).toList();
    }
}
