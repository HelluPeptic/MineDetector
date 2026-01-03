package com.minedetector.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.Set;

public class OreDetector {

    private static final Set<Block> ORE_BLOCKS = Set.of(
            // Only diamond ores and ancient debris
            Blocks.DIAMOND_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.ANCIENT_DEBRIS
    );

    public static boolean isOreBlock(Block block) {
        return ORE_BLOCKS.contains(block);
    }

    public static String getOreName(Block block) {
        return block.getName().getString().toLowerCase()
                .replace("deepslate ", "")
                .replace("nether ", "")
                .replace(" ore", "");
    }
}
