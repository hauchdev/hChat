package dev.hauch.hchat.command;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.IgnoreManager;
import dev.hauch.hchat.manager.MessageHistory;
import dev.hauch.hchat.manager.OfflineMessageStore;
import dev.hauch.hchat.manager.SpyManager;
import dev.hauch.hchat.utils.ChatLogger;
import dev.hauch.hchat.utils.MessageFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

// class MessageCommand
public class MessageCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;
    private final IgnoreManager ignoreManager;
    private final SpyManager spyManager;
    private final MessageHistory messageHistory;
    private final ChatLogger chatLogger;
    private final OfflineMessageStore offlineStore;

    // make MessageCommand
    public MessageCommand(Plugin plugin,
                          PluginConfig config,
                          PluginMessages messages,
                          IgnoreManager ignoreManager,
                          SpyManager spyManager,
                          MessageHistory messageHistory,
                          ChatLogger chatLogger,
                          OfflineMessageStore offlineStore) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.ignoreManager = ignoreManager;
        this.spyManager = spyManager;
        this.messageHistory = messageHistory;
        this.chatLogger = chatLogger;
        this.offlineStore = offlineStore;
    }

    @Override
    // on command
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("player-only")));
            return true;
        }

        if (!player.hasPermission("hchat.message")) {
            player.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(MessageFormatter.format(messages.getString("invalid-usage-message")));
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            org.bukkit.OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);
            if (!offline.hasPlayedBefore()) {
                player.sendMessage(MessageFormatter.format(messages.getString("player-not-found")));
                return true;
            }

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("sender", player.getName());
            placeholders.put("receiver", offline.getName() != null ? offline.getName() : args[0]);
            placeholders.put("message", message);

            String rendered = GsonComponentSerializer.gson()
                    .serialize(MessageFormatter.format(
                            config.getMessageReceiverFormat(), placeholders));
            offlineStore.save(offline.getUniqueId(), rendered);

            placeholders.clear();
            placeholders.put("target", offline.getName() != null ? offline.getName() : args[0]);
            player.sendMessage(MessageFormatter.format(
                    messages.getString("offline-message-saved"), placeholders));
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(MessageFormatter.format(messages.getString("send-yourself")));
            return true;
        }

        if (ignoreManager.isIgnored(target, player)) {
            player.sendMessage(MessageFormatter.format(messages.getString("ignored-by-target")));
            return true;
        }

        if (ignoreManager.isIgnored(player, target) && !player.hasPermission("hchat.bypass.ignore")) {
            player.sendMessage(MessageFormatter.format(messages.getString("you-are-ignoring-player"))
                    .replaceText(b -> b.matchLiteral("{player}").replacement(target.getName())));
            return true;
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("sender", player.getName());
        placeholders.put("receiver", target.getName());
        placeholders.put("message", message);

        Component receiverMsg = MessageFormatter.format(
                config.getMessageReceiverFormat(), placeholders);

        if (config.isMessageReceiverHoverTextEnabled()) {
            Component hover = MessageFormatter.formatHover(
                    config.getMessageReceiverHoverText(), placeholders);
            receiverMsg = receiverMsg.hoverEvent(HoverEvent.showText(hover));
        }

        if (config.isMessagesClickableActionsEnabled()) {
            String replyCmd = config.getMessagesClickableActionsReplyCommand()
                    .replace("{sender}", player.getName());
            receiverMsg = receiverMsg.clickEvent(ClickEvent.suggestCommand(replyCmd));
        }

        target.sendMessage(receiverMsg);

        Component senderMsg = MessageFormatter.format(
                config.getMessageSenderFormat(), placeholders);

        if (config.isMessageSenderHoverTextEnabled()) {
            Component hover = MessageFormatter.formatHover(
                    config.getMessageSenderHoverText(), placeholders);
            senderMsg = senderMsg.hoverEvent(HoverEvent.showText(hover));
        }

        player.sendMessage(senderMsg);

        messageHistory.recordReceived(target.getUniqueId(), player.getUniqueId(), message);

        if (!sender.hasPermission("hchat.bypass-log")) {
            chatLogger.logPrivateMessage(player.getName(), target.getName(), message);
        }

        if (!spyManager.getSpies().isEmpty()) {
            Map<String, String> spyPlaceholders = new HashMap<>();
            spyPlaceholders.put("sender", player.getName());
            spyPlaceholders.put("target", target.getName());
            spyPlaceholders.put("message", message);

            Component spyMsg = MessageFormatter.format(
                    config.getSpyFormat(), spyPlaceholders);

            for (UUID spyUuid : spyManager.getSpies()) {
                Player spy = Bukkit.getPlayer(spyUuid);
                if (spy != null && spy.isOnline() && !spy.equals(player) && !spy.equals(target)) {
                    if (spy.hasPermission("hchat.spy.all") || spy.hasPermission("hchat.spy")) {
                        spy.sendMessage(spyMsg);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String label,
                                                @NotNull String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
