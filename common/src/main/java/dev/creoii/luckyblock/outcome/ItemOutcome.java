package dev.creoii.luckyblock.outcome;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.ContextualProvider;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.Function;

public class ItemOutcome extends Outcome {
    public static final MapCodec<ItemOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(outcome -> outcome.delay),
                createGlobalPosField(Outcome::getPos),
                createGlobalReinitField(Outcome::shouldReinit),
                Codec.either(Identifier.CODEC, ItemStack.CODEC).xmap(either -> {
                    return either.map(identifier -> Registries.ITEM.get(identifier).getDefaultStack(), Function.identity());
                }, Either::right).fieldOf("item").forGetter(outcome -> outcome.stack),
                IntProviders.POSITIVE_CODEC.optionalFieldOf("count").forGetter(outcome -> outcome.count),
                ComponentMap.CODEC.optionalFieldOf("components").forGetter(outcome -> outcome.componentMap),
                VecProvider.VALUE_CODEC.optionalFieldOf("velocity").forGetter(outcome -> outcome.velocity)
        ).apply(instance, ItemOutcome::new);
    });
    private final ItemStack stack;
    private final Optional<IntProvider> count;
    private final Optional<ComponentMap> componentMap;
    private final Optional<VecProvider> velocity;

    public ItemOutcome(int luck, float chance, IntProvider weightProvider, IntProvider delay, Optional<VecProvider> pos, boolean reinit, ItemStack stack, Optional<IntProvider> count, Optional<ComponentMap> componentMap, Optional<VecProvider> velocity) {
        super(OutcomeType.ITEM, luck, chance, weightProvider, delay, pos, reinit);
        this.stack = stack;
        this.count = count;
        this.componentMap = componentMap;
        this.velocity = velocity;
    }

    @Override
    public void run(Context context) {
        Vec3 spawnPos = getPos().isPresent() ? getPos().get().getVec(context) : Vec3.atCenterOf(context.pos());
        Vec3 velocity = null;
        if (this.velocity.isPresent()) {
            velocity = this.velocity.get().getVec(context);
        }
        IntProvider count = ContextualProvider.applyIntContext(this.count.orElse(LuckyBlockCodecs.ONE), context);
        int total = count.sample(context.world().getRandom()) * stack.getCount();

        if (shouldReinit()) {
            for (int i = 0; i < total; ++i) {
                ItemEntity entity = EntityTypes.ITEM.create(context.world(), EntitySpawnReason.NATURAL);
                if (entity != null) {
                    ItemStack newStack = copyStack(stack);

                    componentMap.ifPresent(newStack::applyComponentsFrom);
                    entity.setItem(newStack);
                    entity.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

                    if (velocity != null) {
                        entity.setDeltaMovement(velocity);
                        velocity = this.velocity.get().getVec(context);
                    } else entity.setDeltaMovement(context.world().random.nextDouble() * .2d - .1d, .2d, context.world().random.nextDouble() * .2d - .1d);

                    context.world().addFreshEntity(entity);

                    spawnPos = getPos().isPresent() ? getPos().get().getVec(context) : Vec3.atCenterOf(context.pos());
                }
            }
            return;
        }

        for (int i = 0; i < total / stack.getMaxStackSize(); ++i) {
            ItemEntity entity = EntityTypes.ITEM.create(context.world(), EntitySpawnReason.NATURAL);
            if (entity != null) {
                ItemStack newStack = copyStack(stack);

                newStack.setCount(stack.getMaxStackSize());
                componentMap.ifPresent(newStack::applyComponentsFrom);
                entity.setItem(newStack);
                entity.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

                if (velocity != null) {
                    entity.setDeltaMovement(velocity);
                } else entity.setDeltaMovement(context.world().getRandom().nextDouble() * .2d - .1d, .2d, context.world().getRandom().nextDouble() * .2d - .1d);

                context.world().addFreshEntity(entity);
            }
        }

        if (total % stack.getMaxStackSize() > 0) {
            ItemEntity entity = EntityTypes.ITEM.create(context.world(), EntitySpawnReason.NATURAL);
            if (entity != null) {
                ItemStack remainder = copyStack(stack);

                remainder.setCount(total % stack.getMaxStackSize());
                entity.setItem(remainder);
                componentMap.ifPresent(remainder::applyComponentsFrom);
                entity.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

                if (velocity != null) {
                    entity.setDeltaMovement(this.velocity.get().getVec(context));
                } else entity.setDeltaMovement(context.world().getRandom().nextDouble() * .2d - .1d, .2d, context.world().getRandom().nextDouble() * .2d - .1d);

                context.world().addFreshEntity(entity);
            }
        }
    }

    public static ItemStack copyStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            ItemStack itemStack = new ItemStack(stack.getItem(), stack.getCount(), MergedComponentMap.create(stack.getItem().getComponents(), stack.getComponentChanges()));
            itemStack.setPopTime(stack.getPopTime());
            return itemStack;
        }
    }
}
