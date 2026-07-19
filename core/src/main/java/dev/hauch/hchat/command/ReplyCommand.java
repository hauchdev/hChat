package dev.hauch.hchat.command;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.MessageHistory;
import dev.hauch.hchat.utils.MessageFormatter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

// class ReplyCommand
public class ReplyCommand implements CommandExecutor {

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;
    private final MessageHistory messageHistory;

    // make ReplyCommand
    public ReplyCommand(Plugin plugin,
                        PluginConfig config,
                        PluginMessages messages,
                        MessageHistory messageHistory) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.messageHistory = messageHistory;
    }

    @Override
    // on command
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("player-only")));
            return true;
        }

        if (!player.hasPermission("hchat.reply")) {
            player.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return true;
        }

        if (!messageHistory.hasLastSender(player.getUniqueId())) {
            player.sendMessage(MessageFormatter.format(messages.getString("no-reply-target")));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(MessageFormatter.format(messages.getString("invalid-usage-reply")));
            return true;
        }

        UUID targetUuid = messageHistory.getLastSender(player.getUniqueId());
        Player target = Bukkit.getPlayer(targetUuid);

        if (target == null || !target.isOnline()) {
            player.sendMessage(MessageFormatter.format(messages.getString("player-offline")));
            messageHistory.clear(player.getUniqueId());
            return true;
        }

        String message = String.join(" ", args);
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = target.getName();
        System.arraycopy(args, 0, newArgs, 1, args.length);

        ((JavaPlugin) plugin).getCommand("message").execute(sender, "msg", newArgs);
        return true;
    }
}
