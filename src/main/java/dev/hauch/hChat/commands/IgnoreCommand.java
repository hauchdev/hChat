package dev.hauch.hChat.commands;

import dev.hauch.hChat.HChat;
import dev.hauch.hChat.utils.MessageFormatter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IgnoreCommand implements CommandExecutor, TabCompleter {

    private final HChat plugin;

    public IgnoreCommand(HChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("player-only")));
            return true;
        }

        if (!player.hasPermission("hchat.ignore")) {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("no-permission")));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("invalid-usage-ignore")));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("player-not-found")));
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("cannot-ignore-self")));
            return true;
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", target.getName());

        if (plugin.getIgnoreManager().isIgnored(player, target)) {

            plugin.getIgnoreManager().unignorePlayer(player.getUniqueId(), target.getUniqueId());
            player.sendMessage(MessageFormatter.format(
                    plugin.getMessages().getString("unignored-player"), placeholders));
        } else {

            plugin.getIgnoreManager().ignorePlayer(player.getUniqueId(), target.getUniqueId());
            player.sendMessage(MessageFormatter.format(
                    plugin.getMessages().getString("ignored-player"), placeholders));
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