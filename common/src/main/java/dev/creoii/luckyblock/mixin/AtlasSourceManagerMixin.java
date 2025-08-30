package dev.creoii.luckyblock.mixin;

import com.mojang.serialization.MapCodec;
import dev.creoii.luckyblock.LuckyBlockMod;
import dev.creoii.luckyblock.client.AddonAtlasSource;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AtlasSourceManager.class)
public class AtlasSourceManagerMixin {
    @Shadow @Final private static Codecs.IdMapper<Identifier, MapCodec<? extends AtlasSource>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void gbw$bootstrapLuckyAddonAtlas(CallbackInfo ci) {
        ID_MAPPER.put(Identifier.of(LuckyBlockMod.NAMESPACE, "addon"), AddonAtlasSource.CODEC);
    }
}
