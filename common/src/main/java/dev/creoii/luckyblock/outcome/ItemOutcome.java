package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.Optional;

public class ItemOutcome extends Outcome {
    public static final MapCodec<ItemOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                LuckyBlockCodecs.ITEMSTACK.fieldOf("item").forGetter(outcome -> outcome.stack),
                IntProvider.POSITIVE_CODEC.fieldOf("count").orElse(LuckyBlockCodecs.ONE).forGetter(outcome -> outcome.count),
                ComponentChanges.CODEC.fieldOf("components").orElse(ComponentChanges.EMPTY).forGetter(outcome -> outcome.components),
                LuckyBlockCodecs.VEC_3D.optionalFieldOf("velocity").forGetter(outcome -> outcome.velocity)
        ).apply(instance, ItemOutcome::new);
    });
    private final ItemStack stack;
    private final IntProvider count;
    private final ComponentChanges components;
    private final Optional<String> velocity;

    public ItemOutcome(int luck, float chance, Optional<Integer> delay, Optional<String> pos, ItemStack stack, IntProvider count, ComponentChanges components, Optional<String> velocity) {
        super(OutcomeType.ITEM, luck, chance, delay, pos);
        this.stack = stack;
        this.count = count;
        this.components = components;
        this.velocity = velocity;
    }

    @Override
    public void run(OutcomeContext context) {
        Vec3d spawnPos = getPos().isPresent() ? context.parseVec3d(getPos().get()) : context.pos().toCenterPos();
        int total = count.get(context.world().getRandom()) * stack.getCount();

        for (int i = 0; i < total / stack.getMaxCount(); ++i) {
            ItemEntity entity = EntityType.ITEM.create(context.world());
            if (entity != null) {
                ItemStack newStack = stack.copy();
                if (components != ComponentChanges.EMPTY)
                    newStack.applyChanges(components);

                newStack.setCount(stack.getMaxCount());
                entity.setStack(newStack);
                entity.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);

                if (velocity.isPresent()) {
                    entity.setVelocity(context.parseVec3d(velocity.get()));
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
                entity.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);

                if (velocity.isPresent()) {
                    entity.setVelocity(context.parseVec3d(velocity.get()));
                } else entity.setVelocity(context.world().random.nextDouble() * .2d - .1d, .2d, context.world().random.nextDouble() * .2d - .1d);

                context.world().spawnEntity(entity);
            }
        }
    }
}
