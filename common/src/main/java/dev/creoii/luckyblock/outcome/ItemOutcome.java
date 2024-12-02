package dev.creoii.luckyblock.outcome;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.nbt.ContextualNbtCompound;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.Optional;
import java.util.function.Function;

public class ItemOutcome extends Outcome {
    public static final MapCodec<ItemOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                createGlobalReinitField(Outcome::shouldReinit),
                Codec.either(Identifier.CODEC, ItemStack.CODEC).xmap(either -> {
                    return either.map(identifier -> Registries.ITEM.get(identifier).getDefaultStack(), Function.identity());
                }, Either::right).fieldOf("item").forGetter(outcome -> outcome.stack),
                IntProvider.POSITIVE_CODEC.fieldOf("count").orElse(LuckyBlockCodecs.ONE).forGetter(outcome -> outcome.count),
                ComponentChanges.CODEC.fieldOf("components").orElse(ComponentChanges.EMPTY).forGetter(outcome -> outcome.components),
                ContextualNbtCompound.CODEC.optionalFieldOf("nbt").forGetter(outcome -> outcome.nbt),
                VecProvider.VALUE_CODEC.optionalFieldOf("velocity").forGetter(outcome -> outcome.velocity)
        ).apply(instance, ItemOutcome::new);
    });
    private final ItemStack stack;
    private final IntProvider count;
    private final ComponentChanges components;
    private final Optional<ContextualNbtCompound> nbt;
    private final Optional<VecProvider> velocity;

    public ItemOutcome(int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, boolean reinit, ItemStack stack, IntProvider count, ComponentChanges components, Optional<ContextualNbtCompound> nbt, Optional<VecProvider> velocity) {
        super(OutcomeType.ITEM, luck, chance, weightProvider, delay, pos, reinit);
        this.stack = stack;
        this.count = count;
        this.components = components;
        this.nbt = nbt;
        this.velocity = velocity;
    }

    @Override
    public void run(Context context) {
        Vec3d spawnPos = getPos().isPresent() ? getPos().get().getVec(context) : context.pos().toCenterPos();
        Vec3d velocity = null;
        if (this.velocity.isPresent()) {
            velocity = this.velocity.get().getVec(context);
        }
        int total = count.get(context.world().getRandom()) * stack.getCount();

        if (shouldReinit()) {
            for (int i = 0; i < total; ++i) {
                ItemEntity entity = EntityType.ITEM.create(context.world());
                if (entity != null) {
                    ItemStack newStack = stack.copy();
                    if (components != ComponentChanges.EMPTY)
                        newStack.applyChanges(components);

                    entity.setStack(newStack);
                    nbt.ifPresent(compound -> {
                        compound.setContext(context);
                        entity.readNbt(compound);
                    });
                    entity.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);

                    if (velocity != null) {
                        entity.setVelocity(velocity);
                        velocity = this.velocity.get().getVec(context);
                    } else entity.setVelocity(context.world().random.nextDouble() * .2d - .1d, .2d, context.world().random.nextDouble() * .2d - .1d);

                    context.world().spawnEntity(entity);

                    spawnPos = getPos().isPresent() ? getPos().get().getVec(context) : context.pos().toCenterPos();
                }
            }
            return;
        }

        for (int i = 0; i < total / stack.getMaxCount(); ++i) {
            ItemEntity entity = EntityType.ITEM.create(context.world());
            if (entity != null) {
                ItemStack newStack = stack.copy();
                if (components != ComponentChanges.EMPTY)
                    newStack.applyChanges(components);

                newStack.setCount(stack.getMaxCount());
                entity.setStack(newStack);
                nbt.ifPresent(compound -> {
                    compound.setContext(context);
                    entity.readNbt(compound);
                });
                entity.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);

                if (velocity != null) {
                    entity.setVelocity(velocity);
                } else entity.setVelocity(context.world().random.nextDouble() * .2d - .1d, .2d, context.world().random.nextDouble() * .2d - .1d);

                context.world().spawnEntity(entity);
            }
        }

        if (total % stack.getMaxCount() > 0) {
            ItemEntity entity = EntityType.ITEM.create(context.world());
            if (entity != null) {
                ItemStack remainder = stack.copy();
                if (components != ComponentChanges.EMPTY)
                    remainder.applyChanges(components);

                remainder.setCount(total % stack.getMaxCount());
                entity.setStack(remainder);
                nbt.ifPresent(compound -> {
                    compound.setContext(context);
                    entity.readNbt(compound);
                });
                entity.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);

                if (velocity != null) {
                    entity.setVelocity(this.velocity.get().getVec(context));
                } else entity.setVelocity(context.world().random.nextDouble() * .2d - .1d, .2d, context.world().random.nextDouble() * .2d - .1d);

                context.world().spawnEntity(entity);
            }
        }
    }
}
