package dev.creoii.luckyblock.outcome;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.function.FunctionObjectCodecs;
import dev.creoii.luckyblock.util.function.wrapper.ItemStackWrapper;
import dev.creoii.luckyblock.util.nbt.ContextualNbtCompound;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemOutcome extends Outcome<ItemOutcome.ItemInfo> {
    public static final MapCodec<ItemOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                createGlobalReinitField(Outcome::shouldReinit),
                FunctionObjectCodecs.ITEM_STACK_WRAPPER.fieldOf("stack").forGetter(outcome -> outcome.stack),
                IntProvider.POSITIVE_CODEC.fieldOf("count").orElse(LuckyBlockCodecs.ONE).forGetter(outcome -> outcome.count),
                ComponentChanges.CODEC.fieldOf("components").orElse(ComponentChanges.EMPTY).forGetter(outcome -> outcome.components),
                ContextualNbtCompound.CODEC.optionalFieldOf("nbt").forGetter(outcome -> outcome.nbt),
                VecProvider.VALUE_CODEC.optionalFieldOf("velocity").forGetter(outcome -> outcome.velocity)
        ).apply(instance, ItemOutcome::new);
    });
    private final ItemStackWrapper stack;
    private final IntProvider count;
    private final ComponentChanges components;
    private final Optional<ContextualNbtCompound> nbt;
    private final Optional<VecProvider> velocity;

    public ItemOutcome(int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, boolean reinit, ItemStackWrapper stack, IntProvider count, ComponentChanges components, Optional<ContextualNbtCompound> nbt, Optional<VecProvider> velocity) {
        super(OutcomeType.ITEM, luck, chance, weightProvider, delay, pos, reinit);
        this.stack = stack;
        this.count = count;
        this.components = components;
        this.nbt = nbt;
        this.velocity = velocity;
    }

    @Override
    public Context<ItemInfo> create(Context<ItemInfo> context) {
        Vec3d pos = getPos(context).getVec(context);
        int count = this.count.get(context.world().getRandom());
        List<ItemEntity> itemEntities = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            ItemEntity entity = EntityType.ITEM.create(context.world(), SpawnReason.NATURAL);
            if (entity != null) {
                itemEntities.add(entity);
            }
        }

        return context.withInfo(new ItemInfo(stack, pos, velocity.map(vecProvider -> vecProvider.getVec(context)).orElse(null), itemEntities));
    }

    @Override
    public void run(Context<ItemInfo> context) {
        ItemStack stack = context.info().stack.toStack(this, context);
        int count = context.info().items.size();
        for (int i = 0; i < count; ++i) {
            ItemEntity itemEntity = context.info().items.get(i);
            itemEntity.setStack(stack.copy());

            itemEntity.refreshPositionAndAngles(context.info().pos, 0f, 0f);

            if (context.info().velocity != null) {
                itemEntity.setVelocity(context.info().velocity);
            } else itemEntity.setVelocity(context.world().random.nextDouble() * .2d - .1d, .2d, context.world().random.nextDouble() * .2d - .1d);

            context.world().spawnEntity(itemEntity);
        }
    }

    public static class ItemInfo implements ContextInfo {
        private final ItemStackWrapper stack;
        private final Vec3d pos;
        @Nullable
        private final Vec3d velocity;
        private final List<ItemEntity> items;

        public ItemInfo(ItemStackWrapper stack, Vec3d pos, @Nullable Vec3d velocity, List<ItemEntity> items) {
            this.stack = stack;
            this.pos = pos;
            this.velocity = velocity;
            this.items = items;
        }

        @Override
        public List<Object> getTargets() {
            List<Object> targets = Lists.newArrayList(stack, pos, velocity);
            targets.addAll(items);
            return targets;
        }
    }
}
