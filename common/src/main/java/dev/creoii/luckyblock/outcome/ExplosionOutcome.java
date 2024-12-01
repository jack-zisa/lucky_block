package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import dev.creoii.luckyblock.util.vec.VecProvider;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class ExplosionOutcome extends Outcome {
    public static final MapCodec<ExplosionOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                LuckyBlockCodecs.Explosion.CODEC.fieldOf("explosion").forGetter(outcome -> outcome.explosion)
        ).apply(instance, ExplosionOutcome::new);
    });
    private final LuckyBlockCodecs.Explosion explosion;

    public ExplosionOutcome(int luck, float chance, int delay, Optional<VecProvider> pos, LuckyBlockCodecs.Explosion explosion) {
        super(OutcomeType.EXPLOSION, luck, chance, delay, pos, false);
        this.explosion = explosion;
    }

    @Override
    public void run(Context context) {
        Vec3d pos = getPos().isPresent() ? getPos(context).getVec(context) : context.pos().toCenterPos();
        context.world().createExplosion(context.player(), explosion.getDamageSource(context.world(), context.player()), explosion.getBehavior(context, context.player()), pos.x, pos.y, pos.z, explosion.power(), explosion.createFire(), explosion.getExplosionSourceType(), explosion.particle(), explosion.emitterParticle(), explosion.soundEvent());
    }
}
