package dev.hauch.hchat.command;

import dev.hauch.hchat.config.PluginMessages;
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

// class PingCommand
public class PingCommand implements CommandExecutor, TabCompleter {

    private final PluginMessages messages;

    // make PingCommand
    public PingCommand(PluginMessages messages) {
        this.messages = messages;
    }

    @Override
    // on command
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("player-only")));
            return true;
        }

        if (!player.hasPermission("hchat.ping")) {
            player.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageFormatter.format(messages.getString("ping-message"),
                    Map.of("ping", String.valueOf(player.getPing()))));
            return true;
        }

        if (!player.hasPermission("hchat.ping.others")) {
            player.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(MessageFormatter.format(messages.getString("player-offline")));
            return true;
        }

        player.sendMessage(MessageFormatter.format(messages.getString("ping-others"),
                Map.of("player", target.getName(),
                        "ping", String.valueOf(target.getPing()))));
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
