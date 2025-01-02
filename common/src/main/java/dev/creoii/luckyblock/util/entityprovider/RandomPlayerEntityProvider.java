package dev.creoii.luckyblock.util.entityprovider;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.function.FunctionContainer;
import dev.creoii.luckyblock.function.wrapper.EntityWrapper;
import dev.creoii.luckyblock.outcome.Outcome;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RandomPlayerEntityProvider extends EntityProvider {
    private static final RandomPlayerEntityProvider DEFAULT = new RandomPlayerEntityProvider();
    public static final MapCodec<RandomPlayerEntityProvider> CODEC = MapCodec.unit(DEFAULT);

    protected EntityProviderType<?> getType() {
        return EntityProviderType.RANDOM_PLAYER_ENTITY_PROVIDER;
    }

    @Override
    @Nullable
    public EntityWrapper getEntity(Outcome.Context<?> context, Random random) {
        if (!(context.world() instanceof ServerWorld))
            return null;

        List<ServerPlayerEntity> players = ((ServerWorld) context.world()).getPlayers();
        return new EntityWrapper(players.get(context.random().nextInt(players.size())), FunctionContainer.EMPTY);
    }
}
