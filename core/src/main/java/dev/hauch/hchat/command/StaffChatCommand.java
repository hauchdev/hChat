package dev.hauch.hchat.command;

import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.StaffChatManager;
import dev.hauch.hchat.utils.MessageFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

// class StaffChatCommand
public class StaffChatCommand implements CommandExecutor {

    private final Plugin plugin;
    private final PluginMessages messages;
    private final StaffChatManager staffChatManager;

    // make StaffChatCommand
    public StaffChatCommand(Plugin plugin,
                            PluginMessages messages,
                            StaffChatManager staffChatManager) {
        this.plugin = plugin;
        this.messages = messages;
        this.staffChatManager = staffChatManager;
    }

    @Override
    // on command
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("player-only")));
            return true;
        }

        if (!player.hasPermission("hchat.sc")) {
            player.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageFormatter.format(messages.getString("invalid-usage-sc")));
            return true;
        }

        String message = String.join(" ", args);
        staffChatManager.send(plugin, player, message);
        return true;
    }
}
