package dev.hauch.hchat.command;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.ChannelManager;
import dev.hauch.hchat.model.ChatChannel;
import dev.hauch.hchat.utils.MessageFormatter;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// class ChannelCommand
public class ChannelCommand implements CommandExecutor, TabCompleter {

    private final PluginMessages messages;
    private final ChannelManager channelManager;
    private final PluginConfig config;

    // make ChannelCommand
    public ChannelCommand(PluginMessages messages,
                          ChannelManager channelManager,
                          PluginConfig config) {
        this.messages = messages;
        this.channelManager = channelManager;
        this.config = config;
    }

    @Override
    // on command
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("player-only")));
            return true;
        }

        if (!player.hasPermission("hchat.channel")) {
            player.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
            return true;
        }

        if (args.length == 0) {
            listChannels(player);
            return true;
        }

        ChatChannel channel = channelManager.get(args[0]);
        if (channel == null) {
            player.sendMessage(MessageFormatter.format(messages.getString("channel-not-found"),
                    Map.of("channel", args[0])));
            return true;
        }

        if (!channelManager.canSee(player, channel)) {
            player.sendMessage(MessageFormatter.format(messages.getString("channel-no-access"),
                    Map.of("channel", channel.id())));
            return true;
        }

        channelManager.setActiveChannel(player, channel);
        player.sendMessage(MessageFormatter.format(messages.getString("channel-switched"),
                Map.of("channel", channel.id())));
        if (config.isChannelHintOnSwitch()) {
            showChannelHint(player, channel);
        }
        return true;
    }

    // 3-second action-bar hint with the active channel (uses the
    // channel's own action-bar-hint when set, otherwise the lang message)
    private void showChannelHint(Player player, ChatChannel channel) {
        Component hint = channel.hasActionBarHint()
                ? MessageFormatter.format(channel.actionBarHint())
                : MessageFormatter.format(messages.getString("channel-current"),
                        Map.of("channel", channel.id()));
        player.sendActionBar(hint);
    }

    // list channels
    private void listChannels(Player player) {
        ChatChannel current = channelManager.activeChannel(player);
        player.sendMessage(MessageFormatter.format(messages.getString("channel-current"),
                Map.of("channel", current.id())));

        List<Component> items = new ArrayList<>();
        for (ChatChannel channel : channelManager.all()) {
            if (!channelManager.canSee(player, channel)) continue;
            Component item = MessageFormatter.format("&7[&f" + channel.id() + "&7]");
            item = MessageFormatter.withRunCommand(item, "/channel " + channel.id());
            item = MessageFormatter.withHover(item, MessageFormatter.format(
                    messages.getString("channel-click-hover"),
                    Map.of("channel", channel.id())));
            items.add(item);
        }

        if (!items.isEmpty()) {
            player.sendMessage(MessageFormatter.join(Component.text(" "),
                    items.toArray(new Component[0])));
        }
    }

    @Override
    // on tab complete
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String label,
                                                @NotNull String[] args) {
        if (args.length != 1 || !(sender instanceof Player player)) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        String partial = args[0].toLowerCase(Locale.ROOT);
        for (ChatChannel channel : channelManager.all()) {
            if (channelManager.canSee(player, channel)
                    && channel.id().toLowerCase(Locale.ROOT).startsWith(partial)) {
                result.add(channel.id());
            }
        }
        return result;
    }
}
