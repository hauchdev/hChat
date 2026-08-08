package dev.hauch.hchat.command;

import dev.hauch.hchat.bootstrap.Bootstrap;
import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.LanguageManager;
import dev.hauch.hchat.manager.PlayerLangManager;
import dev.hauch.hchat.update.UpdateChecker;
import dev.hauch.hchat.utils.MessageFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// class HChatCommand
public class HChatCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;
    private final PlayerLangManager playerLangManager;
    private final LanguageManager languageManager;
    private final UpdateChecker updateChecker;

    // make HChatCommand
    public HChatCommand(Plugin plugin,
                        PluginConfig config,
                        PluginMessages messages,
                        PlayerLangManager playerLangManager,
                        LanguageManager languageManager,
                        UpdateChecker updateChecker) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.playerLangManager = playerLangManager;
        this.languageManager = languageManager;
        this.updateChecker = updateChecker;
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
            case "update" -> doUpdate(sender);
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

    // do update
    private void doUpdate(CommandSender sender) {
        if (!sender.hasPermission("hchat.update")) {
            sender.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return;
        }
        sender.sendMessage(MessageFormatter.format(messages.getString("update-checking")));
        updateChecker.checkAsync(() -> {
            if (updateChecker.isUpdateAvailable()) {
                sender.sendMessage(updateChecker.updateMessage(messages));
            } else if (updateChecker.isChecked()) {
                sender.sendMessage(MessageFormatter.format(
                        messages.getString("update-up-to-date"),
                        Map.of("current", updateChecker.currentVersion())));
            } else {
                sender.sendMessage(MessageFormatter.format(
                        messages.getString("update-check-failed")));
            }
        });
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

    @Override
    // on tab complete
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String label,
                                                @NotNull String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase(Locale.ROOT);
            List<String> result = new ArrayList<>();
            for (String sub : subcommands(sender)) {
                if (sub.startsWith(partial)) result.add(sub);
            }
            return result;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("lang")
                && sender.hasPermission("hchat.lang")) {
            String partial = args[1].toLowerCase(Locale.ROOT);
            List<String> result = new ArrayList<>();
            for (String code : languageManager.loadedLanguages().keySet()) {
                if (code.toLowerCase(Locale.ROOT).startsWith(partial)) result.add(code);
            }
            return result;
        }
        return Collections.emptyList();
    }

    // subcommands the sender is allowed to use
    private List<String> subcommands(CommandSender sender) {
        List<String> subs = new ArrayList<>();
        subs.add("help");
        if (sender.hasPermission("hchat.reload")) subs.add("reload");
        if (sender.hasPermission("hchat.lang")) subs.add("lang");
        if (sender.hasPermission("hchat.update")) subs.add("update");
        return subs;
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
        sender.sendMessage(MessageFormatter.format(messages.getString("update-command")));
        sender.sendMessage(MessageFormatter.format(messages.getString("channel-command")));
    }
}
