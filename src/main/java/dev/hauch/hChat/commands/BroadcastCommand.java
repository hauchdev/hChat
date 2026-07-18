package dev.hauch.hChat.commands;

import dev.hauch.hChat.HChat;
import dev.hauch.hChat.utils.BroadcastRenderer;
import dev.hauch.hChat.utils.MessageFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class BroadcastCommand implements CommandExecutor, TabCompleter {

    private final HChat plugin;

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern LEGACY_CODE_PATTERN = Pattern.compile("&[0-9a-fk-orA-FK-OR]");

    public BroadcastCommand(HChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("player-only")));
            return true;
        }

        if (!player.hasPermission("hchat.broadcast")) {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("no-permission")));
            return true;
        }

        if (!plugin.getConfigManager().isBroadcastEnabled()) {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("broadcast-disabled")));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("invalid-usage-broadcast")));
            return true;
        }

        String rawMessage = String.join(" ", args);

        boolean hasColour = HEX_PATTERN.matcher(rawMessage).find()
                || LEGACY_CODE_PATTERN.matcher(rawMessage).find();
        if (hasColour && !player.hasPermission("hchat.broadcast.color")) {
            player.sendMessage(MessageFormatter.format(
                    plugin.getMessages().getString("broadcast-no-permission-color")));
            return true;
        }

        BroadcastRenderer.broadcast(plugin, rawMessage);
        player.sendMessage(MessageFormatter.format(
                plugin.getMessages().getString("message-broadcasted")));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}