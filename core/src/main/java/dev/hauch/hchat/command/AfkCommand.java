package dev.hauch.hchat.command;

import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.AfkManager;
import dev.hauch.hchat.utils.MessageFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// class AfkCommand
public final class AfkCommand implements CommandExecutor {

    private final PluginMessages messages;
    private final AfkManager afkManager;

    // make AfkCommand
    public AfkCommand(PluginMessages messages, AfkManager afkManager) {
        this.messages = messages;
        this.afkManager = afkManager;
    }

    @Override
    // on command
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("player-only")));
            return true;
        }
        if (!player.hasPermission("hchat.afk")) {
            player.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return true;
        }

        // /afk off | /afk on | /afk <message> | /afk (toggle)
        if (args.length == 0) {
            if (afkManager.isAfk(player)) {
                setAfk(player, false, null, "afk-disabled");
            } else {
                setAfk(player, true, null, "afk-enabled");
            }
            return true;
        }

        String first = args[0].toLowerCase(java.util.Locale.ROOT);
        if (first.equals("off")) {
            setAfk(player, false, null, "afk-disabled");
            return true;
        }
        if (first.equals("on")) {
            setAfk(player, true, null, "afk-enabled");
            return true;
        }

        String reason = String.join(" ", args);
        afkManager.setAfk(player, true, reason);
        player.sendMessage(MessageFormatter.format(
                messages.getString("afk-enabled-message"),
                Map.of("message", reason)));
        return true;
    }

    // set afk and show the matching language message
    private void setAfk(Player player, boolean afk, String reason, String langKey) {
        afkManager.setAfk(player, afk, reason);
        player.sendMessage(MessageFormatter.format(messages.getString(langKey)));
    }
}
