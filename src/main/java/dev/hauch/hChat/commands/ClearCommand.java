package dev.hauch.hChat.commands;

import dev.hauch.hChat.HChat;
import dev.hauch.hChat.utils.MessageFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClearCommand implements CommandExecutor {
    private final HChat plugin;

    public ClearCommand(HChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("hchat.clear")) {
            sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("no-permission")));
            return true;
        }

        Component blank = Component.text("\n".repeat(100));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(blank);
        }

        Bukkit.broadcast(MessageFormatter.format(plugin.getMessages().getString("chat-cleared")));
        return true;
    }
}