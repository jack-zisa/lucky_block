package dev.creoii.luckyblock.outcome;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.function.target.CountTarget;
import dev.creoii.luckyblock.function.target.Target;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.function.Function;
import dev.creoii.luckyblock.function.FunctionContainer;
import dev.creoii.luckyblock.function.FunctionObjectCodecs;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.util.vecprovider.VecProvider;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EntityOutcome extends Outcome<EntityOutcome.EntityInfo> implements CountTarget<EntityOutcome> {
    public static final MapCodec<EntityOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                createGlobalReinitField(Outcome::shouldReinit),
                FunctionObjectCodecs.ENTITY_WRAPPER.fieldOf("entity").forGetter(outcome -> outcome.entity),
                FunctionContainer.CODEC.fieldOf("functions").orElse(FunctionContainer.EMPTY).forGetter(outcome -> outcome.functionContainer)
        ).apply(instance, EntityOutcome::new);
    });
    private final EntityWrapper entity;
    private final FunctionContainer functionContainer;
    private IntProvider count;

    public EntityOutcome(int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, boolean reinit, EntityWrapper entity, FunctionContainer functionContainer) {
        super(OutcomeType.ENTITY, luck, chance, weightProvider, delay, pos, reinit);
        this.entity = entity;
        this.functionContainer = functionContainer;
        count = LuckyBlockCodecs.ONE;
    }

    @Override
    public Context<EntityInfo> create(Context<EntityInfo> context) {
        EntityInfo info = new EntityInfo(getPos(context).getVec(context));
        Function.applyPre(functionContainer, this, context.withInfo(info));

        int count = this.count.get(context.random());
        for (int i = 0; i < count; ++i) {
            EntityWrapper entity = this.entity.init(context);
            info.entities.add(entity);
            Function.applyPre(entity.getFunctions(), this, context);
        }

        Function.applyPost(functionContainer, this, context);
        return context;
    }

    @Override
    public void run(Context<EntityInfo> context) {
        for (EntityWrapper entityWrapper : context.info().entities) {
            Function.applyPost(entityWrapper.getFunctions(), this, context);

            entityWrapper.getEntity().refreshPositionAndAngles(context.pos(), entityWrapper.getEntity().getYaw(), entityWrapper.getEntity().getPitch());
            context.world().spawnEntity(entityWrapper.getEntity());
        }
    }

    @Override
    public Target<EntityOutcome> update(Function<Target<?>> function, Object newObject) {
        if (newObject instanceof EntityOutcome newEntityOutcome) {
            //functions.remove(function);
            //newItemOutcome.functions.remove(function);
            return newEntityOutcome;
        }
        throw new IllegalArgumentException("Attempted updating entity outcome target with non-entity-outcome value.");
    }

    @Override
    public EntityOutcome setCount(Outcome<? extends ContextInfo> outcome, Context<? extends ContextInfo> context, IntProvider count) {
        this.count = count;
        return this;
    }

    public class EntityInfo implements ContextInfo {
        private final Vec3d pos;
        private final List<EntityWrapper> entities;
        @Nullable
        private List<Object> cachedTargets;

        public EntityInfo(Vec3d pos, List<EntityWrapper> entities) {
            this.pos = pos;
            this.entities = entities;
            cachedTargets = null;
        }

        public EntityInfo(Vec3d pos) {
            this(pos, Lists.newArrayList());
        }

        @Override
        public List<Object> getTargets() {
            if (cachedTargets != null)
                return cachedTargets;

            List<Object> targets = Lists.newArrayList(EntityOutcome.this, pos);
            targets.addAll(entities);
            return cachedTargets = targets;
        }

        @Override
        public void setTargets(List<Target<?>> targets) {
            cachedTargets = Collections.singletonList(targets);
        }
    }
}
