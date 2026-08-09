package dev.hauch.hchat.command;

import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.MessageHistory;
import dev.hauch.hchat.utils.MessageFormatter;
import org.bukkit.Bukkit;
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
import java.util.Map;
import java.util.UUID;

// class SeenCommand
public class SeenCommand implements CommandExecutor, TabCompleter {

    private final PluginMessages messages;
    private final MessageHistory messageHistory;

    // make SeenCommand
    public SeenCommand(PluginMessages messages,
                       MessageHistory messageHistory) {
        this.messages = messages;
        this.messageHistory = messageHistory;
    }

    @Override
    // on command
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("player-only")));
            return true;
        }

        if (!player.hasPermission("hchat.seen")) {
            player.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageFormatter.format(messages.getString("invalid-usage-seen")));
            return true;
        }

        org.bukkit.OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);
        if (!offline.hasPlayedBefore()) {
            player.sendMessage(MessageFormatter.format(messages.getString("player-not-found")));
            return true;
        }

        long minutes = messageHistory.getMinutesSinceLastReceived(offline.getUniqueId());
        String name = offline.getName() != null ? offline.getName() : args[0];

        if (minutes < 0) {
            player.sendMessage(MessageFormatter.format(messages.getString("seen-none"),
                    Map.of("player", name)));
            return true;
        }

        player.sendMessage(MessageFormatter.format(messages.getString("seen-message"),
                Map.of("player", name, "minutes", String.valueOf(minutes))));
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
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().toLowerCase(Locale.ROOT).startsWith(partial)) {
                result.add(p.getName());
            }
        }
        return result;
    }
}
