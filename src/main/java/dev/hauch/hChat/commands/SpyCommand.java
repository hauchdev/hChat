package dev.hauch.hChat.commands;

import dev.hauch.hChat.HChat;
import dev.hauch.hChat.utils.MessageFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpyCommand implements CommandExecutor {

    private final HChat plugin;

    public SpyCommand(HChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("player-only")));
            return true;
        }

        if (!player.hasPermission("hchat.spy")) {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("no-permission")));
            return true;
        }

        boolean nowEnabled = plugin.getSpyManager().toggleSpy(player.getUniqueId());

        if (nowEnabled) {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("spy-mode-enabled")));
        } else {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("spy-mode-disabled")));
        }

        return true;
    }
}