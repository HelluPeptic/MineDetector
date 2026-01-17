package com.minedetector.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.player.Player;
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
        // If LuckPerms is available, use it
        if (luckPerms != null) {
            try {
                User user = luckPerms.getPlayerAdapter(ServerPlayer.class).getUser(player);
                return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
            } catch (Exception e) {
                // Fall back to operator check if LuckPerms fails
                return isOperator(player);
            }
        }
        
        // Fall back to operator permissions
        return isOperator(player);
    }

    public static boolean hasPermission(CommandSourceStack source, String permission) {
        // If LuckPerms is available and source is a player, use it
        if (luckPerms != null && source.getEntity() instanceof ServerPlayer player) {
            try {
                User user = luckPerms.getPlayerAdapter(ServerPlayer.class).getUser(player);
                return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
            } catch (Exception e) {
                // Fall back to operator check if LuckPerms fails
                return isOperator(player);
            }
        } else if (source.getEntity() instanceof ServerPlayer player) {
            return isOperator(player);
        }
        
        // For non-player sources (console, command blocks), they have full permissions
        return true;
    }

    private static boolean isOperator(ServerPlayer player) {
        // Check if player is an operator using NameAndId conversion
        try {
            // Get the server and check the op list
            var server = player.level().getServer();
            var opList = server.getPlayerList().getOps();
            
            // Convert GameProfile to NameAndId for the lookup
            var nameAndId = new NameAndId(player.getUUID(), player.getGameProfile().name());
            
            // Check if this player is in the operator list
            return opList.get(nameAndId) != null;
        } catch (Exception e) {
            // If we can't check operator status, default to false for security
            return false;
        }
    }

    public static boolean hasDetectionPermission(ServerPlayer player) {
        return hasPermission(player, "oredetector.detectionmessages");
    }

    public static boolean hasOreLogPermission(ServerPlayer player) {
        return hasPermission(player, "oredetector.orelog");
    }
}
