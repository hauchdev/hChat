package dev.hauch.hchat.command;

import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.SlowmodeManager;
import dev.hauch.hchat.utils.MessageFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// class SlowmodeCommand
public class SlowmodeCommand implements CommandExecutor, TabCompleter {

    private final PluginMessages messages;
    private final SlowmodeManager slowmodeManager;

    // make SlowmodeCommand
    public SlowmodeCommand(PluginMessages messages,
                           SlowmodeManager slowmodeManager) {
        this.messages = messages;
        this.slowmodeManager = slowmodeManager;
    }

    @Override
    // on command
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("hchat.slowmode")) {
            sender.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageFormatter.format(messages.getString("slowmode-usage")));
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "off" -> {
                slowmodeManager.setSeconds(0);
                sender.sendMessage(MessageFormatter.format(messages.getString("slowmode-disabled")));
            }
            case "status" -> {
                sender.sendMessage(MessageFormatter.format(messages.getString("slowmode-status"),
                        Map.of("seconds", String.valueOf(slowmodeManager.getSeconds()))));
            }
            default -> {
                try {
                    int seconds = Integer.parseInt(args[0]);
                    if (seconds < 0) {
                        sender.sendMessage(MessageFormatter.format(messages.getString("slowmode-usage")));
                        return true;
                    }
                    slowmodeManager.setSeconds(seconds);
                    sender.sendMessage(MessageFormatter.format(messages.getString("slowmode-enabled"),
                            Map.of("seconds", String.valueOf(seconds))));
                } catch (NumberFormatException e) {
                    sender.sendMessage(MessageFormatter.format(messages.getString("slowmode-usage")));
                }
            }
        }
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
        for (String option : List.of("off", "status")) {
            if (option.startsWith(partial)) result.add(option);
        }
        return result;
    }
}
