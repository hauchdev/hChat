package dev.hauch.hchat.command;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.utils.MessageFormatter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

// class ClearCommand
public class ClearCommand implements CommandExecutor {

    private static final int BLANK_LINES = 100;

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;

    // make ClearCommand
    public ClearCommand(Plugin plugin, PluginConfig config, PluginMessages messages) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
    }

    @Override
    // on command
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("hchat.clear")) {
            sender.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return true;
        }

        Component blank = Component.text("\n".repeat(BLANK_LINES));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(blank);
        }

        Bukkit.broadcast(MessageFormatter.format(messages.getString("chat-cleared")));
        return true;
    }
}
