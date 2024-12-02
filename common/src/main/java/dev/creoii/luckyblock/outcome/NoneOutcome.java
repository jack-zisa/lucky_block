package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.Codec;

public class NoneOutcome extends Outcome {
    public static final Outcome INSTANCE = new NoneOutcome();
    public static final Codec<Outcome> CODEC = Codec.unit(INSTANCE);

    public NoneOutcome() {
        super(OutcomeType.NONE);
    }

    @Override
    public void run(Context context) {}
}
