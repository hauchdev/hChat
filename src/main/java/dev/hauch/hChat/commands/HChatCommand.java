package dev.hauch.hChat.commands;

import dev.hauch.hChat.HChat;
import dev.hauch.hChat.utils.MessageFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HChatCommand implements CommandExecutor {

    private final HChat plugin;

    public HChatCommand(HChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("help")));
            sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("nreload")));
            sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("help-command")));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!sender.hasPermission("hchat.reload")) {
                    sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("no-permission")));
                    return true;
                }
                sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("reloading")));
                try {
                    plugin.reloadPlugin();
                    sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("reloaded")));
                } catch (Exception e) {
                    plugin.getLogger().severe("Reload failed: " + e.getMessage());
                    sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("reload-failed")));
                }
            }
            case "help" -> {
                sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("help")));
                sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("nreload")));
                sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("help-command")));
                sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("message-command")));
                sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("ignore-command")));
                sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("spy-command")));
                sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("reply-command")));
            }
            case "lang" -> {
                if (!sender.hasPermission("hchat.lang")) {
                    sender.sendMessage(MessageFormatter.format(
                            plugin.getMessages().getString("no-permission")));
                    return true;
                }
                if (args.length < 2
                        || !(sender instanceof Player p)) {
                    sender.sendMessage(MessageFormatter.format(
                            plugin.getMessages().getString("invalid-usage-hchat-lang")));
                    return true;
                }
                plugin.getPlayerLangManager().set(p.getUniqueId(), args[1]);
                p.sendMessage(MessageFormatter.format(
                                plugin.getMessages().getString("lang-set"))
                        .replaceText(b -> b.matchLiteral("{lang}").replacement(args[1])));
            }
            default -> {
                sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("help")));
            }
        }
        return true;
    }
}