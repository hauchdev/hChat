package dev.hauch.hchat.command;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.ChannelManager;
import dev.hauch.hchat.service.BroadcastService;
import dev.hauch.hchat.utils.MessageFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

// class BroadcastCommand
public class BroadcastCommand implements CommandExecutor, TabCompleter {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern LEGACY_CODE_PATTERN = Pattern.compile("&[0-9a-fk-orA-FK-OR]");

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;
    private final BroadcastService broadcastService;
    private final ChannelManager channelManager;

    // make BroadcastCommand
    public BroadcastCommand(Plugin plugin,
                            PluginConfig config,
                            PluginMessages messages,
                            BroadcastService broadcastService,
                            ChannelManager channelManager) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.broadcastService = broadcastService;
        this.channelManager = channelManager;
    }

    @Override
    // on command
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("player-only")));
            return true;
        }

        if (!player.hasPermission("hchat.broadcast")) {
            player.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return true;
        }

        if (!config.isBroadcastEnabled()) {
            player.sendMessage(MessageFormatter.format(messages.getString("broadcast-disabled")));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(MessageFormatter.format(messages.getString("invalid-usage-broadcast")));
            return true;
        }

        String rawMessage = String.join(" ", args);

        boolean hasColour = HEX_PATTERN.matcher(rawMessage).find()
                || LEGACY_CODE_PATTERN.matcher(rawMessage).find();
        if (hasColour && !player.hasPermission("hchat.broadcast.color")) {
            player.sendMessage(MessageFormatter.format(
                    messages.getString("broadcast-no-permission-color")));
            return true;
        }

        long cooldownMs = config.getBroadcastCooldownMs();
        long remainingMs = channelManager.cooldownRemaining(
                player, "broadcast", cooldownMs);
        if (remainingMs > 0) {
            player.sendMessage(MessageFormatter.format(
                    messages.getString("broadcast-cooldown"),
                    Map.of("seconds", String.format(Locale.ROOT, "%.1f", remainingMs / 1000.0))));
            return true;
        }
        channelManager.recordCooldown(player, "broadcast");

        broadcastService.broadcast(rawMessage);
        player.sendMessage(MessageFormatter.format(messages.getString("message-broadcasted")));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String label,
                                                @NotNull String[] args) {
        return Collections.emptyList();
    }
}
