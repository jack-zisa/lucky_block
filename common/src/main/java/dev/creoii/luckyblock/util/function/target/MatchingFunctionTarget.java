package dev.creoii.luckyblock.util.function.target;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.luckyblock.outcome.ContextInfo;
import dev.creoii.luckyblock.outcome.Outcome;
import dev.creoii.luckyblock.util.function.wrapper.Wrapper;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;

public class MatchingFunctionTarget extends FunctionTarget<Target<?>> {
    public static final MapCodec<MatchingFunctionTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(Identifier.CODEC.fieldOf("registry").forGetter(target -> target.registryId),
                Identifier.CODEC.optionalFieldOf("id").forGetter(target -> target.id),
                Identifier.CODEC.optionalFieldOf("tag").forGetter(target -> target.tag)
        ).apply(instance, MatchingFunctionTarget::new);
    });
    private final Identifier registryId;
    private final Optional<Identifier> id;
    private final Optional<Identifier> tag;

    public MatchingFunctionTarget(Identifier registryId, Optional<Identifier> id, Optional<Identifier> tag) {
        super(FunctionTargetType.MATCHING);
        this.registryId = registryId;
        this.id = id;
        this.tag = tag;
    }

    @Override
    public List<Target<?>> getTargets(Outcome<? extends ContextInfo> outcome, Outcome.Context<? extends ContextInfo> context) {
        List<Target<?>> targets = Lists.newArrayList();

        RegistryKey<Registry<Object>> registryKey = RegistryKey.ofRegistry(registryId);
        Registry<Object> registry = context.world().getRegistryManager().getOrThrow(registryKey);
        for (Object o : context.info().getTargets()) {
            RegistryEntry<Object> objEntry;
            if (o instanceof Wrapper<?, ?> wrapper) {
                if (id.isEmpty() && tag.isEmpty()) {
                    targets.add(wrapper);
                    continue;
                }

                if ((objEntry = registry.getEntry(wrapper.getObject(context))).hasKeyAndValue()) {
                    if (objEntry.getKey().isEmpty())
                        continue;

                    if (id.isPresent() && objEntry.getKey().get().getValue().equals(id.get())) {
                        System.out.println("match targeting " + id.get());
                        targets.add(wrapper);
                    } else if (tag.isPresent() && objEntry.isIn(TagKey.of(registryKey, tag.get()))) {
                        targets.add(wrapper);
                    }
                }
            }
        }
        return targets;
    }
}
