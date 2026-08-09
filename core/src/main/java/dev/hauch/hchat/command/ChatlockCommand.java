package dev.hauch.hchat.command;

import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.ChatLockManager;
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

// class ChatlockCommand
public class ChatlockCommand implements CommandExecutor, TabCompleter {

    private final PluginMessages messages;
    private final ChatLockManager chatLockManager;

    // make ChatlockCommand
    public ChatlockCommand(PluginMessages messages,
                           ChatLockManager chatLockManager) {
        this.messages = messages;
        this.chatLockManager = chatLockManager;
    }

    @Override
    // on command
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("hchat.chatlock")) {
            sender.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageFormatter.format(messages.getString("chatlock-usage")));
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "on" -> {
                chatLockManager.setLocked(true, sender.getName());
                sender.sendMessage(MessageFormatter.format(messages.getString("chatlock-locked"),
                        Map.of("player", sender.getName())));
            }
            case "off" -> {
                chatLockManager.setLocked(false, sender.getName());
                sender.sendMessage(MessageFormatter.format(messages.getString("chatlock-unlocked"),
                        Map.of("player", sender.getName())));
            }
            case "status" -> {
                if (chatLockManager.isLocked()) {
                    sender.sendMessage(MessageFormatter.format(
                            messages.getString("chatlock-status-locked"),
                            Map.of("player", chatLockManager.getLockedBy() == null
                                    ? "?" : chatLockManager.getLockedBy())));
                } else {
                    sender.sendMessage(MessageFormatter.format(
                            messages.getString("chatlock-status-unlocked")));
                }
            }
            default -> sender.sendMessage(MessageFormatter.format(messages.getString("chatlock-usage")));
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
        for (String option : List.of("on", "off", "status")) {
            if (option.startsWith(partial)) result.add(option);
        }
        return result;
    }
}
