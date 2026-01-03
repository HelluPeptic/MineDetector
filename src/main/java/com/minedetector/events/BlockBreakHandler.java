package com.minedetector.events;

import com.minedetector.storage.OreLogStorage;
import com.minedetector.util.OreDetector;
import com.minedetector.util.PermissionUtil;
import com.minedetector.util.VeinDetector;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BlockBreakHandler implements PlayerBlockBreakEvents.Before {

    // Track processed blocks globally to prevent duplicate notifications
    private static final Set<BlockPos> processedBlocks = new HashSet<>();

    @Override
    public boolean beforeBlockBreak(Level level, Player player, BlockPos pos, BlockState state, @Nullable net.minecraft.world.level.block.entity.BlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return true; // Allow break to continue
        }

        Block block = state.getBlock();
        if (!OreDetector.isOreBlock(block)) {
            return true; // Allow break to continue
        }

        // Skip if this block was already processed as part of a vein
        if (processedBlocks.contains(pos)) {
            return true; // Allow break to continue
        }

        // Detect the entire vein BEFORE the block is broken
        Set<BlockPos> veinBlocks = VeinDetector.getVeinBlocks(serverLevel, pos, block);
        int veinSize = veinBlocks.size();

        // Mark all blocks in this vein as processed
        processedBlocks.addAll(veinBlocks);

        // Clean up old processed blocks to prevent memory leak (keep last 1000)
        if (processedBlocks.size() > 1000) {
            Set<BlockPos> newSet = new HashSet<>();
            processedBlocks.stream().skip(processedBlocks.size() - 500).forEach(newSet::add);
            processedBlocks.clear();
            processedBlocks.addAll(newSet);
        }

        String oreName = OreDetector.getOreName(block);
        String dimension = getDimensionName(serverLevel);

        OreLogStorage.addEntry(
                serverPlayer.getUUID(),
                serverPlayer.getName().getString(),
                oreName,
                veinSize,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                dimension
        );

        sendVeinNotification(serverLevel, serverPlayer, oreName, veinSize);

        return true; // Allow break to continue
    }

    private String getDimensionName(ServerLevel level) {
        String dimensionKey = level.dimension().toString();
        if (dimensionKey.contains("overworld")) {
            return "Overworld";
        }
        if (dimensionKey.contains("nether")) {
            return "Nether";
        }
        if (dimensionKey.contains("end")) {
            return "End";
        }
        return dimensionKey;
    }

    private void sendVeinNotification(ServerLevel level, ServerPlayer miner, String oreName, int veinSize) {
        String message = String.format("§6[OreDetector] §f%s found §e%d %s ore",
                miner.getName().getString(), veinSize, oreName);

        Component messageComponent = Component.literal(message);

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (PermissionUtil.hasDetectionPermission(player)) {
                player.sendSystemMessage(messageComponent);
            }
        }
    }
}
