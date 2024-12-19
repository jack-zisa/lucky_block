package dev.creoii.luckyblock.util.resource;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.resource.*;
import net.minecraft.text.Text;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;

public class LuckyBlockAddonsResourcePackCreator implements ResourcePackProvider {
    public static final LuckyBlockAddonsResourcePackCreator INSTANCE = new LuckyBlockAddonsResourcePackCreator();
    protected static final ResourcePackInfo RESOURCE_PACK_INFO = new ResourcePackInfo(LuckyBlockMod.NAMESPACE, LuckyBlockAddonsResourcePack.DESCRIPTION_TEXT, ResourcePackSource.BUILTIN, Optional.empty());
    public static final ResourcePackSource RESOURCE_PACK_SOURCE = new ResourcePackSource() {
        @Override
        public Text decorate(Text packName) {
            return Text.translatable("pack.nameAndSource", packName, Text.translatable("pack.source.luckyBlockAddon"));
        }

        @Override
        public boolean canBeEnabledLater() {
            return true;
        }
    };

    public static InputStream getDefaultIcon() {
        return null;
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder) {
        profileAdder.accept(ResourcePackProfile.create(
                RESOURCE_PACK_INFO,
                new LuckyBlockAddonsResourcePack.Factory(),
                ResourceType.CLIENT_RESOURCES,
                new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, true)
        ));
    }
}
