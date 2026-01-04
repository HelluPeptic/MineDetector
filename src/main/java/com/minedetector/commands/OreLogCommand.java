package com.minedetector.commands;

import com.minedetector.storage.OreLogEntry;
import com.minedetector.storage.OreLogStorage;
import com.minedetector.util.PermissionUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OreLogCommand {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd HH:mm:ss");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("orelog")
                        .then(Commands.argument("hours", IntegerArgumentType.integer(1, 168))
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();

                                    if (!(source.getEntity() instanceof ServerPlayer player)) {
                                        source.sendFailure(Component.literal("This command can only be run by a player."));
                                        return 0;
                                    }

                                    if (!PermissionUtil.hasOreLogPermission(player)) {
                                        source.sendFailure(Component.literal("§cYou don't have permission to view ore logs."));
                                        return 0;
                                    }

                                    int hours = IntegerArgumentType.getInteger(context, "hours");
                                    List<OreLogEntry> entries = OreLogStorage.getEntriesWithinHours(hours);

                                    if (entries.isEmpty()) {
                                        source.sendSuccess(() -> Component.literal(String.format("§eNo ore mining detected in the last %d hours.", hours)), false);
                                        return 1;
                                    }

                                    source.sendSuccess(() -> Component.literal(String.format("§6=== Ore Log - Last %d Hours ====", hours)), false);
                                    source.sendSuccess(() -> Component.literal(String.format("§7Found %d mining events:", entries.size())), false);

                                    for (OreLogEntry entry : entries) {
                                        Component logMessage = createLogMessage(entry);
                                        source.sendSuccess(() -> logMessage, false);
                                    }

                                    return 1;
                                })
                        )
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();

                            if (!(source.getEntity() instanceof ServerPlayer player)) {
                                source.sendFailure(Component.literal("This command can only be run by a player."));
                                return 0;
                            }

                            if (!PermissionUtil.hasOreLogPermission(player)) {
                                source.sendFailure(Component.literal("§cYou don't have permission to view ore logs."));
                                return 0;
                            }

                            List<OreLogEntry> entries = OreLogStorage.getEntriesWithinHours(24);

                            if (entries.isEmpty()) {
                                source.sendSuccess(() -> Component.literal("§eNo ore mining detected in the last 24 hours."), false);
                                return 1;
                            }

                            source.sendSuccess(() -> Component.literal("§6==== Ore Log - Last 24 Hours ===="), false);
                            source.sendSuccess(() -> Component.literal(String.format("§7Found %d mining events:", entries.size())), false);

                            for (OreLogEntry entry : entries) {
                                Component logMessage = createLogMessage(entry);
                                source.sendSuccess(() -> logMessage, false);
                            }

                            return 1;
                        })
        );

        // Register custom teleport command that won't trigger security dialog
        dispatcher.register(
                Commands.literal("oretp")
                        .then(Commands.argument("x", IntegerArgumentType.integer())
                                .then(Commands.argument("y", IntegerArgumentType.integer())
                                        .then(Commands.argument("z", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    CommandSourceStack source = context.getSource();

                                                    if (!(source.getEntity() instanceof ServerPlayer player)) {
                                                        source.sendFailure(Component.literal("This command can only be used by players."));
                                                        return 0;
                                                    }

                                                    if (!PermissionUtil.hasOreLogPermission(player)) {
                                                        source.sendFailure(Component.literal("§cYou don't have permission to use this teleport command."));
                                                        return 0;
                                                    }

                                                    int x = IntegerArgumentType.getInteger(context, "x");
                                                    int y = IntegerArgumentType.getInteger(context, "y");
                                                    int z = IntegerArgumentType.getInteger(context, "z");

                                                    // Teleport the player
                                                    player.teleportTo(x + 0.5, y, z + 0.5);

                                                    source.sendSuccess(() -> Component.literal("§aTeleported to ore mining location: " + x + ", " + y + ", " + z), false);
                                                    return 1;
                                                })
                                        )
                                )
                        )
        );
    }

    private static Component createLogMessage(OreLogEntry entry) {
        String timeStr = entry.getTimestamp().format(FORMATTER);
        String message = String.format("§7[%s] §f%s §emined %dx %s §fat §b%d, %d, %d §7(%s)",
                timeStr,
                entry.getPlayerName(),
                entry.getOreCount(),
                entry.getOreName(),
                entry.getX(),
                entry.getY(),
                entry.getZ(),
                entry.getDimension());

        // Create the clickable component using the correct nested record classes
        try {
            String teleportCommand = String.format("/oretp %d %d %d", entry.getX(), entry.getY(), entry.getZ());

            // Use RunCommand with custom oretp command (won't trigger security dialog)
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, teleportCommand);
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("§aClick to teleport"));

            // Create the component with style containing both events
            Style style = Style.EMPTY
                    .withClickEvent(clickEvent)
                    .withHoverEvent(hoverEvent);

            return Component.literal(message).withStyle(style);
        } catch (Exception e) {
            // Fallback to regular message if anything goes wrong
            return Component.literal(message + " §8[Coords: " + entry.getX() + ", " + entry.getY() + ", " + entry.getZ() + "]");
        }
    }
}
