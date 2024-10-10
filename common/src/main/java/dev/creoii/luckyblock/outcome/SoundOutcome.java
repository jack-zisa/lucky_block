package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.util.LuckyBlockCodecs;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class SoundOutcome extends Outcome {
    public static final MapCodec<SoundOutcome> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(createGlobalLuckField(Outcome::getLuck),
                createGlobalChanceField(Outcome::getChance),
                createGlobalDelayField(Outcome::getDelay),
                createGlobalPosField(Outcome::getPos),
                SoundEvent.CODEC.fieldOf("sound_event").forGetter(outcome -> outcome.soundEvent),
                LuckyBlockCodecs.DOUBLE.fieldOf("volume").orElse("1").forGetter(outcome -> outcome.volume),
                LuckyBlockCodecs.DOUBLE.fieldOf("pitch").orElse("1").forGetter(outcome -> outcome.pitch)
        ).apply(instance, SoundOutcome::new);
    });
    private final SoundEvent soundEvent;
    private final String volume;
    private final String pitch;

    public SoundOutcome(int luck, float chance, Optional<Integer> delay, Optional<String> pos, SoundEvent soundEvent, String volume, String pitch) {
        super(OutcomeType.SOUND, luck, chance, delay, pos);
        this.soundEvent = soundEvent;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void run(OutcomeContext context) {
        Vec3d pos = getPos().isPresent() ? context.parseVec3d(getPos().get()) : context.pos().toCenterPos();
        context.world().playSound(context.player(), pos.x, pos.y, pos.z, soundEvent, SoundCategory.NEUTRAL, (float) context.parseDouble(volume), (float) context.parseDouble(pitch));
    }
}
