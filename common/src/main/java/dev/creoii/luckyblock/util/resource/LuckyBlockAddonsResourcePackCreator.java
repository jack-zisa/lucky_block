package dev.creoii.luckyblock.util.resource;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;

import java.io.InputStream;
import java.util.function.Consumer;

public class LuckyBlockAddonsResourcePackCreator implements ResourcePackProvider {
    public static final LuckyBlockAddonsResourcePackCreator INSTANCE = new LuckyBlockAddonsResourcePackCreator();

    public static InputStream getDefaultIcon() {
        return LuckyBlockMod.luckyBlockManager.getIcon();
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder) {
        profileAdder.accept(LuckyBlockMod.luckyBlockManager.createResourcePack());
    }
}
