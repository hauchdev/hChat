package dev.hauch.hchat.command;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.SpyManager;
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

// class SpyCommand
public class SpyCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;
    private final SpyManager spyManager;

    // make SpyCommand
    public SpyCommand(Plugin plugin,
                      PluginConfig config,
                      PluginMessages messages,
                      SpyManager spyManager) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.spyManager = spyManager;
    }

    @Override
    // on command
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("player-only")));
            return true;
        }

        if (!player.hasPermission("hchat.spy")) {
            player.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return true;
        }

        boolean nowEnabled = spyManager.toggleSpy(player.getUniqueId());

        if (nowEnabled) {
            player.sendMessage(MessageFormatter.format(messages.getString("spy-mode-enabled")));
        } else {
            player.sendMessage(MessageFormatter.format(messages.getString("spy-mode-disabled")));
        }

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
