package dev.creoii.luckyblock.outcome;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.function.Function;
import dev.creoii.luckyblock.util.function.FunctionContainer;
import dev.creoii.luckyblock.util.function.FunctionObjectCodecs;
import dev.creoii.luckyblock.util.function.target.CountTarget;
import dev.creoii.luckyblock.util.function.target.Target;
import dev.creoii.luckyblock.util.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.util.function.wrapper.ItemStackWrapper;
import dev.creoii.luckyblock.util.vecprovider.VecProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemOutcome extends Outcome<ItemOutcome.ItemInfo> implements CountTarget<ItemOutcome> {
    public static final MapCodec<ItemOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                createGlobalReinitField(Outcome::shouldReinit),
                FunctionObjectCodecs.ITEM_STACK_WRAPPER.fieldOf("stack").forGetter(outcome -> outcome.stack),
                FunctionContainer.CODEC.fieldOf("functions").orElse(FunctionContainer.EMPTY).forGetter(outcome -> outcome.functionContainer)
        ).apply(instance, ItemOutcome::new);
    });
    private final ItemStackWrapper stack;
    private final FunctionContainer functionContainer;
    private IntProvider count;

    public ItemOutcome(int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, boolean reinit, ItemStackWrapper stack, FunctionContainer functionContainer) {
        super(OutcomeType.ITEM, luck, chance, weightProvider, delay, pos, reinit);
        this.stack = stack;
        this.functionContainer = functionContainer;
        count = LuckyBlockCodecs.ONE;
    }

    @Override
    public Context<ItemInfo> create(Context<ItemInfo> context) {
        ItemInfo info = new ItemInfo(getPos(context).getVec(context));
        Function.applyPre(functionContainer, this, context.withInfo(info));

        for (int i = 0; i < this.count.get(context.random()); ++i) {
            ItemEntity itemEntity = EntityType.ITEM.create(context.world(), SpawnReason.NATURAL);
            if (itemEntity != null) {
                ItemStackWrapper stackWrapper = stack.init(context);
                info.stacks.add(stackWrapper);
                Function.applyPre(stackWrapper.getFunctionContainer(), this, context);
            }
        }

        Function.applyPost(functionContainer, this, context);
        return context;
    }

    @Override
    public void run(Context<ItemInfo> context) {
        for (int i = 0; i < context.info().stacks.size(); ++i) {
            ItemStackWrapper stackWrapper = context.info().stacks.get(i);
            ItemEntity itemEntity = EntityType.ITEM.create(context.world(), SpawnReason.NATURAL);
            if (itemEntity != null) {
                Function.applyPost(stackWrapper.getFunctionContainer(), this, context);
                itemEntity.setStack(stackWrapper.getStack());
                itemEntity.refreshPositionAndAngles(context.info().pos, itemEntity.getYaw(), itemEntity.getPitch());
                context.world().spawnEntity(itemEntity);
            }
        }
    }

    @Override
    public Target<ItemOutcome> update(Function<Target<?>> function, Object newObject) {
        if (newObject instanceof ItemOutcome newItemOutcome) {
            //functions.remove(function);
            //newItemOutcome.functions.remove(function);
            return newItemOutcome;
        }
        throw new IllegalArgumentException("Attempted updating item outcome target with non-item-outcome value.");
    }

    @Override
    public ItemOutcome setCount(Outcome<? extends ContextInfo> outcome, Context<? extends ContextInfo> context, IntProvider count) {
        this.count = count;
        return this;
    }

    public class ItemInfo implements ContextInfo {
        private final List<ItemStackWrapper> stacks;
        private final Vec3d pos;

        public ItemInfo(List<ItemStackWrapper> stacks, Vec3d pos) {
            this.stacks = stacks;
            this.pos = pos;
        }

        public ItemInfo(Vec3d pos) {
            this(Lists.newArrayList(), pos);
        }

        @Override
        public List<Object> getTargets() {
            List<Object> targets = Lists.newArrayList(ItemOutcome.this, pos);
            targets.addAll(stacks);
            return targets;
        }
    }
}
