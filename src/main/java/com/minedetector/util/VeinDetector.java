package com.minedetector.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class VeinDetector {

    private static final int MAX_VEIN_SIZE = 100; // Prevent infinite loops

    // All 26 adjacent positions (including diagonals)
    private static final int[][] ADJACENT_OFFSETS = {
        // Same level (y=0)
        {-1, 0, -1}, {-1, 0, 0}, {-1, 0, 1},
        {0, 0, -1}, {0, 0, 1},
        {1, 0, -1}, {1, 0, 0}, {1, 0, 1},
        // Above level (y=1)
        {-1, 1, -1}, {-1, 1, 0}, {-1, 1, 1},
        {0, 1, -1}, {0, 1, 0}, {0, 1, 1},
        {1, 1, -1}, {1, 1, 0}, {1, 1, 1},
        // Below level (y=-1)
        {-1, -1, -1}, {-1, -1, 0}, {-1, -1, 1},
        {0, -1, -1}, {0, -1, 0}, {0, -1, 1},
        {1, -1, -1}, {1, -1, 0}, {1, -1, 1}
    };

    public static int detectVeinSize(ServerLevel level, BlockPos startPos, Block oreType) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toCheck = new LinkedList<>();
        toCheck.offer(startPos);

        int veinSize = 0;

        while (!toCheck.isEmpty() && veinSize < MAX_VEIN_SIZE) {
            BlockPos pos = toCheck.poll();

            if (visited.contains(pos)) {
                continue;
            }

            visited.add(pos);

            // Check if this position contains the same ore type
            if (level.getBlockState(pos).getBlock() == oreType) {
                veinSize++;

                // Check all 26 adjacent blocks (including diagonals)
                for (int[] offset : ADJACENT_OFFSETS) {
                    BlockPos adjacentPos = pos.offset(offset[0], offset[1], offset[2]);
                    if (!visited.contains(adjacentPos)) {
                        toCheck.offer(adjacentPos);
                    }
                }
            }
        }

        return veinSize;
    }

    public static Set<BlockPos> getVeinBlocks(ServerLevel level, BlockPos startPos, Block oreType) {
        Set<BlockPos> veinBlocks = new HashSet<>();
        Queue<BlockPos> toCheck = new LinkedList<>();
        toCheck.offer(startPos);

        while (!toCheck.isEmpty() && veinBlocks.size() < MAX_VEIN_SIZE) {
            BlockPos pos = toCheck.poll();

            if (veinBlocks.contains(pos)) {
                continue;
            }

            // Check if this position contains the same ore type
            if (level.getBlockState(pos).getBlock() == oreType) {
                veinBlocks.add(pos);

                // Check all 26 adjacent blocks (including diagonals)
                for (int[] offset : ADJACENT_OFFSETS) {
                    BlockPos adjacentPos = pos.offset(offset[0], offset[1], offset[2]);
                    if (!veinBlocks.contains(adjacentPos)) {
                        toCheck.offer(adjacentPos);
                    }
                }
            }
        }

        return veinBlocks;
    }
}
