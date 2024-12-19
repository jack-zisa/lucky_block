package dev.creoii.luckyblock.fabric.client;

import dev.creoii.luckyblock.client.LuckyBlockClient;
import net.fabricmc.api.ClientModInitializer;

public final class LuckyBlockFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LuckyBlockClient.initClient();
    }
}
