package dev.hauch.hChat.commands;

import dev.hauch.hChat.HChat;
import dev.hauch.hChat.utils.MessageFormatter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class MessageCommand implements CommandExecutor, TabCompleter {

    private final HChat plugin;

    public MessageCommand(HChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(plugin.getMessages().getString("player-only")));
            return true;
        }

        if (!player.hasPermission("hchat.message")) {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("no-permission")));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("invalid-usage-message")));
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            org.bukkit.OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);
            if (!offline.hasPlayedBefore()) {
                player.sendMessage(MessageFormatter.format(
                        plugin.getMessages().getString("player-not-found")));
                return true;
            }

            // Player is offline but has played before → save and notify.
            var placeholders = new HashMap<String, String>();
            placeholders.put("sender", player.getName());
            placeholders.put("receiver", offline.getName() != null ? offline.getName() : args[0]);
            placeholders.put("message", message);

            String rendered = net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson()
                    .serialize(MessageFormatter.format(
                            plugin.getConfigManager().getMessageReceiverFormat(), placeholders));
            plugin.getOfflineMessageStore().save(offline.getUniqueId(), rendered);

            placeholders.clear();
            placeholders.put("target", offline.getName() != null ? offline.getName() : args[0]);
            player.sendMessage(MessageFormatter.format(
                    plugin.getMessages().getString("offline-message-saved"), placeholders));
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(MessageFormatter.format(plugin.getMessages().getString("send-yourself")));
            return true;
        }

        if (plugin.getIgnoreManager().isIgnored(target, player)) {
            player.sendMessage(MessageFormatter.format(
                    plugin.getMessages().getString("ignored-by-target")));
            return true;
        }

        if (plugin.getIgnoreManager().isIgnored(player, target) && !player.hasPermission("hchat.bypass.ignore")) {
            player.sendMessage(MessageFormatter.format(
                            plugin.getMessages().getString("you-are-ignoring-player"))
                    .replaceText(b -> b.matchLiteral("{player}").replacement(target.getName())));
            return true;
        }


        Map<String, String> senderPlaceholders = new HashMap<>();
        senderPlaceholders.put("sender", player.getName());
        senderPlaceholders.put("receiver", target.getName());
        senderPlaceholders.put("message", message);

        Map<String, String> receiverPlaceholders = new HashMap<>();
        receiverPlaceholders.put("sender", player.getName());
        receiverPlaceholders.put("receiver", target.getName());
        receiverPlaceholders.put("message", message);

        Component receiverMsg = MessageFormatter.format(
                plugin.getConfigManager().getMessageReceiverFormat(), receiverPlaceholders);

        if (plugin.getConfigManager().isMessageReceiverHoverTextEnabled()) {
            Component hover = MessageFormatter.formatHover(
                    plugin.getConfigManager().getMessageReceiverHoverText(), receiverPlaceholders);
            receiverMsg = receiverMsg.hoverEvent(
                    net.kyori.adventure.text.event.HoverEvent.showText(hover));
        }

        if (plugin.getConfigManager().isMessagesClickableActionsEnabled()) {
            String replyCmd = plugin.getConfigManager().getMessagesClickableActionsReplyCommand()
                    .replace("{sender}", player.getName());
            receiverMsg = receiverMsg.clickEvent(
                    net.kyori.adventure.text.event.ClickEvent.suggestCommand(replyCmd));
        }

        target.sendMessage(receiverMsg);

        Component senderMsg = MessageFormatter.format(
                plugin.getConfigManager().getMessageSenderFormat(), senderPlaceholders);

        if (plugin.getConfigManager().isMessageSenderHoverTextEnabled()) {
            Component hover = MessageFormatter.formatHover(
                    plugin.getConfigManager().getMessageSenderHoverText(), senderPlaceholders);
            senderMsg = senderMsg.hoverEvent(
                    net.kyori.adventure.text.event.HoverEvent.showText(hover));
        }

        player.sendMessage(senderMsg);

        plugin.getMessageHistory().recordReceived(target.getUniqueId(),
                player.getUniqueId(), message);

        if (!sender.hasPermission("hchat.bypass-log")) {
            plugin.getChatLogger().logPrivateMessage(player.getName(), target.getName(), message);
        }

        if (!plugin.getSpyManager().getSpies().isEmpty()) {
            Map<String, String> spyPlaceholders = new HashMap<>();
            spyPlaceholders.put("sender", player.getName());
            spyPlaceholders.put("target", target.getName());
            spyPlaceholders.put("message", message);

            Component spyMsg = MessageFormatter.format(
                    plugin.getConfigManager().getSpyFormat(), spyPlaceholders);

            for (UUID spyUuid : plugin.getSpyManager().getSpies()) {
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