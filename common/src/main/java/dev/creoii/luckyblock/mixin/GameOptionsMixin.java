package dev.creoii.luckyblock.mixin;

import dev.creoii.luckyblock.LuckyBlockMod;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Shadow
    public List<String> resourcePacks;

    @Inject(method = "load", at = @At("RETURN"))
    private void lucky$loadBuiltInResourcePacks(CallbackInfo ci) {
        List<String> resourcePacks = new ArrayList<>(this.resourcePacks);
        resourcePacks.addFirst(LuckyBlockMod.NAMESPACE);
        this.resourcePacks = resourcePacks;
    }
}