package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.Optional;

public class ParticleOutcome extends Outcome {
    public static final MapCodec<ParticleOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                createGlobalReinitField(Outcome::shouldReinit),
                ParticleTypes.TYPE_CODEC.fieldOf("particle_type").forGetter(outcome -> outcome.particle),
                IntProvider.POSITIVE_CODEC.fieldOf("count").orElse(LuckyBlockCodecs.ONE).forGetter(outcome -> outcome.count),
                LuckyBlockCodecs.VEC_3D.optionalFieldOf("velocity").forGetter(outcome -> outcome.velocity)
        ).apply(instance, ParticleOutcome::new);
    });
    private final ParticleEffect particle;
    private final IntProvider count;
    private final Optional<String> velocity;

    public ParticleOutcome(int luck, float chance, Optional<Integer> delay, Optional<String> pos, boolean reinit, ParticleEffect particle, IntProvider count, Optional<String> velocity) {
        super(OutcomeType.PARTICLE, luck, chance, delay, pos, reinit);
        this.particle = particle;
        this.count = count;
        this.velocity = velocity;
    }

    @Override
    public void run(OutcomeContext context) {
        Vec3d pos = getPos().isPresent() ? context.parseVec3d(getPos().get()) : context.pos().toCenterPos();

        Vec3d velocity = Vec3d.ZERO;
        if (this.velocity.isPresent()) {
            velocity = context.parseVec3d(this.velocity.get());
        }

        for (int i = 0; i < count.get(context.world().getRandom()); ++i) {
            context.world().addParticle(particle, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);

            if (shouldReinit()) {
                pos = getPos().isPresent() ? context.parseVec3d(getPos().get()) : context.pos().toCenterPos();
            }
        }
    }
}
