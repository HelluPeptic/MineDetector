package com.minedetector.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OreLogStorage {

    private static final List<OreLogEntry> oreLog = new CopyOnWriteArrayList<>();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();
    private static final String LOG_FILE_NAME = "ore_mining_log.json";
    private static Path logFilePath;
    private static ScheduledExecutorService saveExecutor;

    public static void init() {
        // Set up the log file path in the server's root directory
        logFilePath = Path.of(".").resolve(LOG_FILE_NAME);

        // Load existing data from file
        loadFromFile();

        // Set up periodic saving (every 5 minutes)
        saveExecutor = Executors.newSingleThreadScheduledExecutor();
        saveExecutor.scheduleAtFixedRate(OreLogStorage::saveToFile, 5, 5, TimeUnit.MINUTES);

        // Register server lifecycle events
        ServerLifecycleEvents.SERVER_STOPPING.register(OreLogStorage::onServerStopping);
    }

    public static void addEntry(UUID playerId, String playerName, String oreName,
            int oreCount, int x, int y, int z, String dimension) {
        OreLogEntry entry = new OreLogEntry(
                LocalDateTime.now(), playerId, playerName, oreName,
                oreCount, x, y, z, dimension
        );

        oreLog.add(entry);

        // Save immediately for important data (async)
        saveExecutor.submit(OreLogStorage::saveToFile);
    }

    public static List<OreLogEntry> getEntriesWithinHours(int hours) {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(hours);
        List<OreLogEntry> filteredEntries = new ArrayList<>();

        for (OreLogEntry entry : oreLog) {
            if (entry.getTimestamp().isAfter(cutoff)) {
                filteredEntries.add(entry);
            }
        }

        // Sort by timestamp, newest first
        filteredEntries.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return filteredEntries;
    }

    public static List<OreLogEntry> getAllEntries() {
        List<OreLogEntry> allEntries = new ArrayList<>(oreLog);
        allEntries.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return allEntries;
    }

    private static void loadFromFile() {
        if (!Files.exists(logFilePath)) {
            System.out.println("[OreDetector] No existing log file found, starting with empty log.");
            return;
        }

        try {
            String json = Files.readString(logFilePath);
            List<OreLogEntry> loadedEntries = gson.fromJson(json, new TypeToken<List<OreLogEntry>>() {
            }.getType());

            if (loadedEntries != null) {
                oreLog.addAll(loadedEntries);
                System.out.println("[OreDetector] Loaded " + loadedEntries.size() + " ore mining entries from file.");
            }
        } catch (Exception e) {
            System.err.println("[OreDetector] Failed to load ore log from file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void saveToFile() {
        try {
            String json = gson.toJson(new ArrayList<>(oreLog));
            Files.writeString(logFilePath, json);
        } catch (Exception e) {
            System.err.println("[OreDetector] Failed to save ore log to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void onServerStopping(MinecraftServer server) {
        System.out.println("[OreDetector] Server stopping, saving ore log...");
        saveToFile();

        if (saveExecutor != null) {
            saveExecutor.shutdown();
            try {
                if (!saveExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    saveExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                saveExecutor.shutdownNow();
            }
        }

        System.out.println("[OreDetector] Ore log saved successfully with " + oreLog.size() + " entries.");
    }
}
