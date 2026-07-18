package dev.hauch.hChat.commands;

import dev.hauch.hChat.HChat;
import dev.hauch.hChat.utils.MessageFormatter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class ReplyCommand implements CommandExecutor {

    private final HChat plugin;

    public ReplyCommand(HChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("player-only")));
            return true;
        }

        if (!player.hasPermission("hchat.reply")) {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("no-permission")));
            return true;
        }

        if (!plugin.getMessageHistory().hasLastSender(player.getUniqueId())) {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("no-reply-target")));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("invalid-usage-reply")));
            return true;
        }

        UUID targetUuid = plugin.getMessageHistory().getLastSender(player.getUniqueId());
        Player target = Bukkit.getPlayer(targetUuid);

        if (target == null || !target.isOnline()) {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("player-offline")));
            plugin.getMessageHistory().clear(player.getUniqueId());
            return true;
        }

        String message = String.join(" ", args);
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = target.getName();
        System.arraycopy(args, 0, newArgs, 1, args.length);

        plugin.getCommand("message").execute(sender, "msg", newArgs);
        return true;
    }
}