package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.Optional;

public class ParticleOutcome extends Outcome {
    public static final MapCodec<ParticleOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalWeightField(Outcome::getWeightProvider),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                createGlobalReinitField(Outcome::shouldReinit),
                ParticleTypes.TYPE_CODEC.fieldOf("particle_type").forGetter(outcome -> outcome.particle),
                IntProvider.POSITIVE_CODEC.fieldOf("count").orElse(LuckyBlockCodecs.ONE).forGetter(outcome -> outcome.count),
                VecProvider.VALUE_CODEC.optionalFieldOf("velocity").forGetter(outcome -> outcome.velocity),
                FloatProvider.VALUE_CODEC.optionalFieldOf("speed").forGetter(outcome -> outcome.speed)
        ).apply(instance, ParticleOutcome::new);
    });
    private final ParticleEffect particle;
    private final IntProvider count;
    private final Optional<VecProvider> velocity;
    private final Optional<FloatProvider> speed;

    public ParticleOutcome(int luck, float chance, IntProvider weightProvider, int delay, Optional<VecProvider> pos, boolean reinit, ParticleEffect particle, IntProvider count, Optional<VecProvider> velocity, Optional<FloatProvider> speed) {
        super(OutcomeType.PARTICLE, luck, chance, weightProvider, delay, pos, reinit);
        this.particle = particle;
        this.count = count;
        this.velocity = velocity;
        this.speed = speed;
    }

    @Override
    public void run(Context context) {
        Vec3d pos = getPos().isPresent() ? getPos().get().getVec(context) : context.pos().toCenterPos();
        float speed = this.speed.map(floatProvider -> floatProvider.get(context.world().getRandom())).orElse(0f);

        Vec3d velocity = Vec3d.ZERO;
        if (this.velocity.isPresent()) {
            velocity = this.velocity.get().getVec(context);
        }

        for (ServerPlayerEntity serverPlayer : context.world().getServer().getPlayerManager().getPlayerList()) {
            for (int i = 0; i < count.get(context.world().getRandom()); ++i) {
                ((ServerWorld) context.world()).spawnParticles(serverPlayer, particle, false, pos.x, pos.y, pos.z, 1, velocity.x, velocity.y, velocity.z, speed);

                if (shouldReinit()) {
                    pos = getPos().isPresent() ? getPos().get().getVec(context) : context.pos().toCenterPos();
                    if (this.velocity.isPresent()) {
                        velocity = this.velocity.get().getVec(context);
                    }
                }
            }
        }
    }
}
