package com.minedetector.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class PermissionUtil {

    @Nullable
    private static LuckPerms luckPerms;

    static {
        try {
            luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            luckPerms = null;
        }
    }

    public static boolean hasPermission(ServerPlayer player, String permission) {
        // For now, just return true for all players - can be enhanced later
        return true;
    }

    public static boolean hasDetectionPermission(ServerPlayer player) {
        return hasPermission(player, "oredetector.detectionmessages");
    }

    public static boolean hasOreLogPermission(ServerPlayer player) {
        return hasPermission(player, "oredetector.orelog");
    }
}
