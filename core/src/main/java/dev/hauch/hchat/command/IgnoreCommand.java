package dev.hauch.hchat.command;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.IgnoreManager;
import dev.hauch.hchat.utils.MessageFormatter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// class IgnoreCommand
public class IgnoreCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;
    private final IgnoreManager ignoreManager;

    // make IgnoreCommand
    public IgnoreCommand(Plugin plugin,
                         PluginConfig config,
                         PluginMessages messages,
                         IgnoreManager ignoreManager) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.ignoreManager = ignoreManager;
    }

    @Override
    // on command
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("player-only")));
            return true;
        }

        if (!player.hasPermission("hchat.ignore")) {
            player.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(MessageFormatter.format(messages.getString("invalid-usage-ignore")));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(MessageFormatter.format(messages.getString("player-not-found")));
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(MessageFormatter.format(messages.getString("cannot-ignore-self")));
            return true;
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", target.getName());

        if (ignoreManager.isIgnored(player, target)) {
            ignoreManager.unignorePlayer(player.getUniqueId(), target.getUniqueId());
            player.sendMessage(MessageFormatter.format(
                    messages.getString("unignored-player"), placeholders));
        } else {
            ignoreManager.ignorePlayer(player.getUniqueId(), target.getUniqueId());
            player.sendMessage(MessageFormatter.format(
                    messages.getString("ignored-player"), placeholders));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String label,
                                                @NotNull String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
