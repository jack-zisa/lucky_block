package dev.creoii.luckyblock.outcome;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.function.Function;
import dev.creoii.luckyblock.util.function.FunctionObjectCodecs;
import dev.creoii.luckyblock.util.function.target.CountTarget;
import dev.creoii.luckyblock.util.function.target.Target;
import dev.creoii.luckyblock.util.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.util.function.wrapper.ItemStackWrapper;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
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
                FunctionObjectCodecs.ITEM_STACK_WRAPPER.fieldOf("stack").forGetter(outcome -> outcome.stackProvider),
                Function.CODEC.listOf().fieldOf("functions").orElse(List.of()).forGetter(outcome -> outcome.functions)
        ).apply(instance, ItemOutcome::new);
    });
    private final ItemStackWrapper stackProvider;
    private final List<Function<?>> functions;
    private IntProvider count;

    public ItemOutcome(int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, boolean reinit, ItemStackWrapper stackProvider, List<Function<?>> functions) {
        super(OutcomeType.ITEM, luck, chance, weightProvider, delay, pos, reinit);
        this.stackProvider = stackProvider;
        this.functions = functions;
        count = LuckyBlockCodecs.ONE;
    }

    @Override
    public Context<ItemInfo> create(Context<ItemInfo> context) {
        ItemInfo info = new ItemInfo();
        Function.applyPre(functions, this, context.withInfo(info));

        Vec3d pos = getPos(context).getVec(context);

        List<EntityWrapper> itemEntities = new ArrayList<>();
        for (int i = 0; i < this.count.get(context.world().getRandom()); ++i) {
            ItemEntity itemEntity = EntityType.ITEM.create(context.world(), SpawnReason.NATURAL);
            if (itemEntity != null) {
                itemEntities.add(new EntityWrapper(itemEntity, List.of()));
            }
        }

        info.stack = stackProvider;
        info.pos = pos;
        info.items = itemEntities;

        Function.applyPost(functions, this, context.withInfo(info));
        return context.withInfo(info);
    }

    @Override
    public void run(Context<ItemInfo> context) {
        Function.applyAll(context.info().stack.functions(), this, context);
        ItemStack stack = context.info().stack.stackProvider().get(context.world().getRandom());

        int count = context.info().items.size();
        for (int i = 0; i < count; ++i) {
            Entity entity = context.info().items.get(i).getEntity();
            if (entity instanceof ItemEntity itemEntity) {
                itemEntity.setStack(stack.copy());
                itemEntity.refreshPositionAndAngles(context.info().pos, 0f, 0f);
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
        private ItemStackWrapper stack;
        private Vec3d pos;
        private List<EntityWrapper> items;

        public ItemInfo(ItemStackWrapper stack, Vec3d pos, List<EntityWrapper> items) {
            this.stack = stack;
            this.pos = pos;
            this.items = items;
        }

        public ItemInfo() {
            this(null, null, List.of());
        }

        @Override
        public List<Object> getTargets() {
            List<Object> targets = Lists.newArrayList(ItemOutcome.this, stack, pos);
            targets.addAll(items);
            return targets;
        }
    }
}
