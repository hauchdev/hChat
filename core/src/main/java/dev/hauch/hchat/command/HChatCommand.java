package dev.hauch.hchat.command;

import dev.hauch.hchat.bootstrap.Bootstrap;
import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.ChatReplay;
import dev.hauch.hchat.manager.IgnoreManager;
import dev.hauch.hchat.manager.LanguageManager;
import dev.hauch.hchat.manager.OfflineMessageStore;
import dev.hauch.hchat.manager.PlayerLangManager;
import dev.hauch.hchat.update.UpdateChecker;
import dev.hauch.hchat.utils.MessageFormatter;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// class HChatCommand
public class HChatCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;
    private final PlayerLangManager playerLangManager;
    private final LanguageManager languageManager;
    private final UpdateChecker updateChecker;
    private final IgnoreManager ignoreManager;
    private final OfflineMessageStore offlineStore;
    private final ChatReplay chatReplay;

    // make HChatCommand
    public HChatCommand(Plugin plugin,
                        PluginConfig config,
                        PluginMessages messages,
                        PlayerLangManager playerLangManager,
                        LanguageManager languageManager,
                        UpdateChecker updateChecker,
                        IgnoreManager ignoreManager,
                        OfflineMessageStore offlineStore,
                        ChatReplay chatReplay) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.playerLangManager = playerLangManager;
        this.languageManager = languageManager;
        this.updateChecker = updateChecker;
        this.ignoreManager = ignoreManager;
        this.offlineStore = offlineStore;
        this.chatReplay = chatReplay;
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
            case "about" -> doAbout(sender);
            case "ignore" -> doIgnoreList(sender, args);
            case "mail" -> doMail(sender, args);
            case "replay" -> doReplay(sender, args);
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

    // do about
    private void doAbout(CommandSender sender) {
        if (!sender.hasPermission("hchat.help")) {
            sender.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return;
        }
        sender.sendMessage(MessageFormatter.format(messages.getString("about-header")));
        sender.sendMessage(MessageFormatter.format(messages.getString("about-version"),
                Map.of("version", plugin.getDescription().getVersion())));
        sender.sendMessage(MessageFormatter.format(messages.getString("about-authors")));
        sender.sendMessage(MessageFormatter.format(messages.getString("about-website")));
        sender.sendMessage(MessageFormatter.format(messages.getString("about-features")));
    }

    // do ignore list
    private void doIgnoreList(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("player-only")));
            return;
        }
        if (args.length < 2 || !args[1].equalsIgnoreCase("list")) {
            player.sendMessage(MessageFormatter.format(messages.getString("invalid-usage-ignore")));
            return;
        }
        if (!player.hasPermission("hchat.ignore")) {
            player.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return;
        }
        Set<UUID> ignored = ignoreManager.getIgnoredPlayers(player.getUniqueId());
        if (ignored.isEmpty()) {
            player.sendMessage(MessageFormatter.format(messages.getString("ignore-list-empty")));
            return;
        }
        player.sendMessage(MessageFormatter.format(messages.getString("ignore-list-header")));
        for (UUID uuid : ignored) {
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            if (name == null) continue;
            player.sendMessage(MessageFormatter.withRunCommand(
                    MessageFormatter.format(messages.getString("ignore-list-item"),
                            Map.of("player", name)),
                    "/ignore " + name));
        }
    }

    // do mail
    private void doMail(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("player-only")));
            return;
        }
        if (args.length < 2) {
            player.sendMessage(MessageFormatter.format(messages.getString("mail-usage")));
            return;
        }
        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "read" -> {
                List<OfflineMessageStore.OfflineMessage> list =
                        offlineStore.drain(player.getUniqueId());
                if (list.isEmpty()) {
                    player.sendMessage(MessageFormatter.format(messages.getString("mail-empty")));
                    return;
                }
                player.sendMessage(MessageFormatter.format(messages.getString("mail-header")));
                for (OfflineMessageStore.OfflineMessage message : list) {
                    player.sendMessage(GsonComponentSerializer.gson()
                            .deserialize(message.rendered()));
                }
            }
            case "clear" -> {
                offlineStore.clear(player.getUniqueId());
                player.sendMessage(MessageFormatter.format(messages.getString("mail-cleared")));
            }
            case "send" -> {
                if (args.length < 5) {
                    player.sendMessage(MessageFormatter.format(messages.getString("mail-usage")));
                    return;
                }
                String[] newArgs = new String[args.length - 2];
                newArgs[0] = args[2];
                System.arraycopy(args, 3, newArgs, 1, args.length - 3);
                ((JavaPlugin) plugin).getCommand("message").execute(sender, "msg", newArgs);
            }
            default -> player.sendMessage(MessageFormatter.format(messages.getString("mail-usage")));
        }
    }

    // do replay
    private void doReplay(CommandSender sender, String[] args) {
        if (!sender.hasPermission("hchat.help")) {
            sender.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return;
        }
        if (args.length >= 2 && args[1].equalsIgnoreCase("clear-buffer")) {
            if (!sender.hasPermission("hchat.clear")) {
                sender.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
                return;
            }
            chatReplay.clear();
            sender.sendMessage(MessageFormatter.format(messages.getString("replay-cleared")));
            return;
        }
        List<String> lines = args.length >= 2
                ? chatReplay.lastFor(args[1], config.getReplayCapacity())
                : chatReplay.last(config.getReplayCapacity());
        if (lines.isEmpty()) {
            sender.sendMessage(MessageFormatter.format(messages.getString("replay-empty")));
            return;
        }
        sender.sendMessage(MessageFormatter.format(messages.getString("replay-header")));
        for (String line : lines) {
            sender.sendMessage(MessageFormatter.format(line));
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
        if (args.length == 2 && args[0].equalsIgnoreCase("mail")
                && sender instanceof Player) {
            String partial = args[1].toLowerCase(Locale.ROOT);
            List<String> result = new ArrayList<>();
            for (String option : List.of("read", "clear", "send")) {
                if (option.startsWith(partial)) result.add(option);
            }
            return result;
        }
        return Collections.emptyList();
    }

    // subcommands the sender is allowed to use
    private List<String> subcommands(CommandSender sender) {
        List<String> subs = new ArrayList<>();
        subs.add("help");
        subs.add("about");
        subs.add("mail");
        subs.add("replay");
        if (sender.hasPermission("hchat.reload")) subs.add("reload");
        if (sender.hasPermission("hchat.lang")) subs.add("lang");
        if (sender.hasPermission("hchat.update")) subs.add("update");
        if (sender.hasPermission("hchat.ignore")) subs.add("ignore");
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
