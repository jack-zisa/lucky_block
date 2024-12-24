package dev.creoii.luckyblock.client;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.util.Identifier;

public class LuckyBlockClient {
    public static final AtlasSourceType ADDON_ATLAS_SOURCE = new AtlasSourceType(AddonAtlasSource.CODEC);

    public static void initClient() {
        AtlasSourceManager.SOURCE_TYPE_BY_ID.put(Identifier.of(LuckyBlockMod.NAMESPACE, "addon"), ADDON_ATLAS_SOURCE);
    }
}
