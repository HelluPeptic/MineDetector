package com.minedetector;

import com.minedetector.commands.OreLogCommand;
import com.minedetector.config.MineDetectorConfig;
import com.minedetector.events.BlockBreakHandler;
import com.minedetector.storage.OreLogStorage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MineDetectorMod implements ModInitializer {

    public static final String MOD_ID = "minedetector";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("MineDetector mod initializing...");

        // Initialize config
        MineDetectorConfig.init();

        // Initialize storage
        OreLogStorage.init();

        // Register block break handler
        PlayerBlockBreakEvents.BEFORE.register(new BlockBreakHandler());

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            OreLogCommand.register(dispatcher);
        });

        LOGGER.info("MineDetector mod initialized!");
    }
}
