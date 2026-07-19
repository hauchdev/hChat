package dev.hauch.hchat.command;

import dev.hauch.hchat.bootstrap.Bootstrap;
import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.PlayerLangManager;
import dev.hauch.hchat.utils.MessageFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

// class HChatCommand
public class HChatCommand implements CommandExecutor {

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;
    private final PlayerLangManager playerLangManager;

    // make HChatCommand
    public HChatCommand(Plugin plugin,
                        PluginConfig config,
                        PluginMessages messages,
                        PlayerLangManager playerLangManager) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.playerLangManager = playerLangManager;
    }

    @Override
    // on command
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> doReload(sender);
            case "help" -> sendHelp(sender);
            case "lang" -> doLang(sender, args);
            default -> sendHelp(sender);
        }
        return true;
    }

    // do reload
    private void doReload(CommandSender sender) {
        if (!sender.hasPermission("hchat.reload")) {
            sender.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return;
        }
        sender.sendMessage(MessageFormatter.format(messages.getString("reloading")));
        try {
            Bootstrap.reload();
            sender.sendMessage(MessageFormatter.format(messages.getString("reloaded")));
        } catch (Exception e) {
            plugin.getLogger().severe("Reload failed: " + e.getMessage());
            sender.sendMessage(MessageFormatter.format(messages.getString("reload-failed")));
        }
    }

    // do lang
    private void doLang(CommandSender sender, String[] args) {
        if (!sender.hasPermission("hchat.lang")) {
            sender.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return;
        }
        if (args.length < 2 || !(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("invalid-usage-hchat-lang")));
            return;
        }
        playerLangManager.set(player.getUniqueId(), args[1]);
        player.sendMessage(MessageFormatter.format(messages.getString("lang-set"))
                .replaceText(b -> b.matchLiteral("{lang}").replacement(args[1])));
    }

    // send help
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(MessageFormatter.format(messages.getString("help")));
        sender.sendMessage(MessageFormatter.format(messages.getString("nreload")));
        sender.sendMessage(MessageFormatter.format(messages.getString("help-command")));
        sender.sendMessage(MessageFormatter.format(messages.getString("message-command")));
        sender.sendMessage(MessageFormatter.format(messages.getString("ignore-command")));
        sender.sendMessage(MessageFormatter.format(messages.getString("spy-command")));
        sender.sendMessage(MessageFormatter.format(messages.getString("reply-command")));
    }
}
