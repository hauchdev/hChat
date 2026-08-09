package dev.hauch.hchat.command;

import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.DndManager;
import dev.hauch.hchat.utils.MessageFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

// class DndCommand
public class DndCommand implements CommandExecutor, TabCompleter {

    private final PluginMessages messages;
    private final DndManager dndManager;

    // make DndCommand
    public DndCommand(PluginMessages messages,
                      DndManager dndManager) {
        this.messages = messages;
        this.dndManager = dndManager;
    }

    @Override
    // on command
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("player-only")));
            return true;
        }

        if (!player.hasPermission("hchat.dnd")) {
            player.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return true;
        }

        boolean enabled;
        if (args.length == 0) {
            enabled = dndManager.toggleDnd(player.getUniqueId());
        } else if (args[0].equalsIgnoreCase("on")) {
            dndManager.enableDnd(player.getUniqueId());
            enabled = true;
        } else if (args[0].equalsIgnoreCase("off")) {
            dndManager.disableDnd(player.getUniqueId());
            enabled = false;
        } else {
            player.sendMessage(MessageFormatter.format(messages.getString("invalid-usage-dnd")));
            return true;
        }

        player.sendMessage(MessageFormatter.format(messages.getString(
                enabled ? "dnd-enabled" : "dnd-disabled")));
        return true;
    }

    @Override
    // on tab complete
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String label,
                                                @NotNull String[] args) {
        if (args.length != 1) return Collections.emptyList();
        String partial = args[0].toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<>();
        for (String option : List.of("on", "off")) {
            if (option.startsWith(partial)) result.add(option);
        }
        return result;
    }
}
