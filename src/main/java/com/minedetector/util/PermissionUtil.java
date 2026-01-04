package com.minedetector.util;

import net.minecraft.server.level.ServerPlayer;

public class PermissionUtil {

    private static Boolean luckPermsAvailable = null;
    private static Object luckPermsInstance = null;

    static {
        try {
            Class.forName("net.luckperms.api.LuckPermsProvider");
            luckPermsAvailable = true;
            try {
                Class<?> providerClass = Class.forName("net.luckperms.api.LuckPermsProvider");
                luckPermsInstance = providerClass.getMethod("get").invoke(null);
            } catch (Exception e) {
                luckPermsAvailable = false;
                luckPermsInstance = null;
            }
        } catch (ClassNotFoundException e) {
            luckPermsAvailable = false;
            luckPermsInstance = null;
        }
    }

    public static boolean hasPermission(ServerPlayer player, String permission) {
        // For now, just return true for all players - can be enhanced later with LuckPerms integration
        return true;
    }

    public static boolean hasDetectionPermission(ServerPlayer player) {
        return hasPermission(player, "oredetector.detectionmessages");
    }

    public static boolean hasOreLogPermission(ServerPlayer player) {
        return hasPermission(player, "oredetector.orelog");
    }
}
