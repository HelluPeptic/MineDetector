package com.minedetector.storage;

import java.time.LocalDateTime;
import java.util.UUID;

public class OreLogEntry {

    private final LocalDateTime timestamp;
    private final UUID playerId;
    private final String playerName;
    private final String oreName;
    private final int oreCount;
    private final int x, y, z;
    private final String dimension;

    public OreLogEntry(LocalDateTime timestamp, UUID playerId, String playerName,
            String oreName, int oreCount, int x, int y, int z, String dimension) {
        this.timestamp = timestamp;
        this.playerId = playerId;
        this.playerName = playerName;
        this.oreName = oreName;
        this.oreCount = oreCount;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getOreName() {
        return oreName;
    }

    public int getOreCount() {
        return oreCount;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getDimension() {
        return dimension;
    }
}
