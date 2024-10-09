package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
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
                IntProvider.POSITIVE_CODEC.fieldOf("count").orElse(LuckyBlockCodecs.ONE).forGetter(outcome -> outcome.count)
        ).apply(instance, ItemOutcome::new);
    });
    private final ItemStack stack;
    private final IntProvider count;

    public ItemOutcome(int luck, float chance, Optional<Integer> delay, Optional<String> pos, ItemStack stack, IntProvider count) {
        super(OutcomeType.ITEM, luck, chance, delay, pos);
        this.stack = stack;
        this.count = count;
    }

    @Override
    public void run(OutcomeContext context) {
        Vec3d spawnPos = getPos().isPresent() ? context.parseVec3d(getPos().get()) : context.pos().toCenterPos();
        int total = count.get(context.world().getRandom()) * stack.getCount();

        for (int i = 0; i < total / stack.getMaxCount(); ++i) {
            ItemEntity entity = EntityType.ITEM.create(context.world());
            if (entity != null) {
                ItemStack newStack = stack.copy();
                newStack.setCount(stack.getMaxCount());
                entity.setStack(newStack);
                entity.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
                entity.setVelocity(context.world().random.nextDouble() * .2d - .1d, .2d, context.world().random.nextDouble() * .2d - .1d);
                context.world().spawnEntity(entity);
            }
        }

        if (total % stack.getMaxCount() > 0) {
            ItemEntity entity = EntityType.ITEM.create(context.world());
            if (entity != null) {
                ItemStack remainder = stack.copy();
                remainder.setCount(total % stack.getMaxCount());
                entity.setStack(remainder);
                entity.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
                entity.setVelocity(context.world().random.nextDouble() * .2d - .1d, .2d, context.world().random.nextDouble() * .2d - .1d);
                context.world().spawnEntity(entity);
            }
        }
    }
}
