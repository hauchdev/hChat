package dev.hauch.hchat.api;

import dev.hauch.hchat.api.platform.Platform;
import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.ChannelManager;
import dev.hauch.hchat.manager.DndManager;
import dev.hauch.hchat.manager.IgnoreManager;
import dev.hauch.hchat.manager.MessageHistory;
import dev.hauch.hchat.manager.OfflineMessageStore;
import dev.hauch.hchat.manager.PlayerLangManager;
import dev.hauch.hchat.manager.SpyManager;
import dev.hauch.hchat.model.ChatChannel;
import dev.hauch.hchat.placeholder.HChatPlaceholderExpansion;
import dev.hauch.hchat.service.BroadcastService;
import dev.hauch.hchat.utils.MessageFormatter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.logging.Logger;

// class HChatProvider
public final class HChatProvider {

    private static volatile HChatProvider instance;

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;
    private final IgnoreManager ignoreManager;
    private final SpyManager spyManager;
    private final OfflineMessageStore offlineStore;
    private final PlayerLangManager playerLangManager;
    private final HChatPlaceholderExpansion placeholders;
    private final ChannelManager channelManager;
    private final DndManager dndManager;
    private final MessageHistory messageHistory;
    private final BroadcastService broadcastService;
    private final Logger logger;

    public HChatProvider(Plugin plugin,
                         PluginConfig config,
                         PluginMessages messages,
                         IgnoreManager ignoreManager,
                         SpyManager spyManager,
                         OfflineMessageStore offlineStore,
                         PlayerLangManager playerLangManager,
                         HChatPlaceholderExpansion placeholders,
                         ChannelManager channelManager,
                         DndManager dndManager,
                         MessageHistory messageHistory,
                         BroadcastService broadcastService) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.config = Objects.requireNonNull(config, "config");
        this.messages = Objects.requireNonNull(messages, "messages");
        this.ignoreManager = Objects.requireNonNull(ignoreManager, "ignoreManager");
        this.spyManager = Objects.requireNonNull(spyManager, "spyManager");
        this.offlineStore = Objects.requireNonNull(offlineStore, "offlineStore");
        this.playerLangManager = Objects.requireNonNull(playerLangManager, "playerLangManager");
        this.placeholders = Objects.requireNonNull(placeholders, "placeholders");
        this.channelManager = Objects.requireNonNull(channelManager, "channelManager");
        this.dndManager = Objects.requireNonNull(dndManager, "dndManager");
        this.messageHistory = Objects.requireNonNull(messageHistory, "messageHistory");
        this.broadcastService = Objects.requireNonNull(broadcastService, "broadcastService");
        this.logger = plugin.getLogger();
    }

    // get data
    public static HChatProvider get() {
        HChatProvider value = instance;
        if (value == null) {
            throw new IllegalStateException(
                    "hChat is not running – HChatProvider.install() never happened.");
        }
        return value;
    }

    // install data
    public static void install(HChatProvider provider) {
        instance = Objects.requireNonNull(provider, "provider");
    }

    // uninstall data
    public static void uninstall() {
        instance = null;
        Platform.reset();
    }

    // plugin data
    public Plugin plugin() {
        return plugin;
    }

    // config data
    public PluginConfig config() {
        return config;
    }

    // messages data
    public PluginMessages messages() {
        return messages;
    }

    // ignore manager
    public IgnoreManager ignoreManager() {
        return ignoreManager;
    }

    // spy manager
    public SpyManager spyManager() {
        return spyManager;
    }

    // offline message store
    public OfflineMessageStore offlineMessageStore() {
        return offlineStore;
    }

    // player lang manager
    public PlayerLangManager playerLangManager() {
        return playerLangManager;
    }

    // placeholders data
    public HChatPlaceholderExpansion placeholders() {
        return placeholders;
    }

    // channel manager
    public ChannelManager channelManager() {
        return channelManager;
    }

    // dnd manager
    public DndManager dndManager() {
        return dndManager;
    }

    // message history
    public MessageHistory messageHistory() {
        return messageHistory;
    }

    // broadcast service
    public BroadcastService broadcastService() {
        return broadcastService;
    }

    // publish a message into a channel, honoring its see-permission
    public void publishToChannel(ChatChannel channel, String message) {
        if (channel == null) return;
        String format = channel.hasFormat()
                ? channel.format() : config.getDefaultChatFormat();
        String resolved = format
                .replace("{player}", "Console")
                .replace("{message}", message);
        Component rendered = MessageFormatter.format(resolved);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (channelManager.canSee(p, channel)) {
                p.sendMessage(rendered);
            }
        }
    }

    // logger data
    public Logger logger() {
        return logger;
    }

    // is running
    public boolean isRunning() {
        return instance != null;
    }
}
