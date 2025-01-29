package dev.creoii.luckyblock.outcome;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.function.target.Target;

import java.util.List;

public class NoneOutcome extends Outcome<NoneOutcome.NoneInfo> {
    public static final Outcome<NoneOutcome.NoneInfo> INSTANCE = new NoneOutcome();
    public static final MapCodec<Outcome<NoneOutcome.NoneInfo>> CODEC = MapCodec.unit(INSTANCE);

    public NoneOutcome() {
        super(OutcomeType.NONE);
    }

    @Override
    public Context<NoneInfo> create(Context<NoneInfo> context) {
        return context.withInfo(new NoneInfo());
    }

    @Override
    public void run(Context<NoneOutcome.NoneInfo> context) {}

    public record NoneInfo() implements ContextInfo {
        @Override
        public List<Object> getTargets() {
            return List.of();
        }

        @Override
        public void setTargets(List<Target<?>> targets) {
        }
    }
}
