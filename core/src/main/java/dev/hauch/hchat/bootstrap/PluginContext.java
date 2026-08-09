package dev.hauch.hchat.bootstrap;

import dev.hauch.hchat.api.platform.PlatformAdapter;
import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.AfkManager;
import dev.hauch.hchat.manager.AutoBroadcastManager;
import dev.hauch.hchat.manager.ChatLockManager;
import dev.hauch.hchat.manager.ChatReplay;
import dev.hauch.hchat.manager.ChannelManager;
import dev.hauch.hchat.manager.DndManager;
import dev.hauch.hchat.manager.MentionManager;
import dev.hauch.hchat.manager.IgnoreManager;
import dev.hauch.hchat.manager.LanguageManager;
import dev.hauch.hchat.manager.MessageHistory;
import dev.hauch.hchat.manager.OfflineMessageStore;
import dev.hauch.hchat.manager.PlayerLangManager;
import dev.hauch.hchat.manager.SlowmodeManager;
import dev.hauch.hchat.manager.SpyManager;
import dev.hauch.hchat.manager.StaffChatManager;
import dev.hauch.hchat.placeholder.HChatPlaceholderExpansion;
import dev.hauch.hchat.registry.CommandRegistry;
import dev.hauch.hchat.registry.ListenerRegistry;
import dev.hauch.hchat.registry.ManagerRegistry;
import dev.hauch.hchat.registry.ServiceRegistry;
import dev.hauch.hchat.service.ConfigurationService;
import dev.hauch.hchat.storage.IgnoreStorage;
import dev.hauch.hchat.storage.PlayerChannelStorage;
import dev.hauch.hchat.storage.PlayerLangStorage;
import dev.hauch.hchat.update.UpdateChecker;
import dev.hauch.hchat.utils.ChatLogger;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
// class PluginContext

/**
 * The fully assembled hChat runtime context.
 * <p>
 * Internal to the bootstrap flow, but public so the Premium edition module
 * can register commands, listeners and managers into the existing
 * registries during {@code PremiumModule#install(PluginContext)}.
 */
public final class PluginContext {

    public final Plugin plugin;
    public final PluginConfig config;
    public final PluginMessages messages;
    public final ConfigurationService configurationService;

    public final IgnoreStorage ignoreStorage;
    public final PlayerLangStorage playerLangStorage;
    public final PlayerChannelStorage playerChannelStorage;

    public final ChannelManager channelManager;
    public final IgnoreManager ignoreManager;
    public final SpyManager spyManager;
    public final MessageHistory messageHistory;
    public final OfflineMessageStore offlineMessageStore;
    public final PlayerLangManager playerLangManager;
    public final LanguageManager languageManager;
    public final AutoBroadcastManager autoBroadcastManager;
    public final ChatLogger chatLogger;
    public final DndManager dndManager;
    public final SlowmodeManager slowmodeManager;
    public final ChatLockManager chatLockManager;
    public final MentionManager mentionManager;
    public final StaffChatManager staffChatManager;
    public final ChatReplay chatReplay;
    public final AfkManager afkManager;

    public final CommandRegistry commandRegistry;
    public final ListenerRegistry listenerRegistry;
    public final ServiceRegistry serviceRegistry;
    public final ManagerRegistry managerRegistry;

    public final PlatformAdapter platformAdapter;

    public final HChatPlaceholderExpansion placeholderExpansion;
    public final UpdateChecker updateChecker;

    public final boolean discordSrvEnabled;

    public PluginContext(Plugin plugin,
                         PlatformAdapter platformAdapter,
                         PluginConfig config,
                         PluginMessages messages,
                         ConfigurationService configurationService,
                         IgnoreStorage ignoreStorage,
                         PlayerLangStorage playerLangStorage,
                         PlayerChannelStorage playerChannelStorage,
                         ChannelManager channelManager,
                         IgnoreManager ignoreManager,
                         SpyManager spyManager,
                         MessageHistory messageHistory,
                         OfflineMessageStore offlineMessageStore,
                         PlayerLangManager playerLangManager,
                         LanguageManager languageManager,
                         AutoBroadcastManager autoBroadcastManager,
                         ChatLogger chatLogger,
                         DndManager dndManager,
                         SlowmodeManager slowmodeManager,
                         ChatLockManager chatLockManager,
                         MentionManager mentionManager,
                         StaffChatManager staffChatManager,
                         ChatReplay chatReplay,
                         AfkManager afkManager,
                         CommandRegistry commandRegistry,
                         ListenerRegistry listenerRegistry,
                         ServiceRegistry serviceRegistry,
                         ManagerRegistry managerRegistry,
                         HChatPlaceholderExpansion placeholderExpansion,
                         UpdateChecker updateChecker,
                         boolean discordSrvEnabled) {
        this.plugin = Objects.requireNonNull(plugin);
        this.platformAdapter = Objects.requireNonNull(platformAdapter);
        this.config = Objects.requireNonNull(config);
        this.messages = Objects.requireNonNull(messages);
        this.configurationService = Objects.requireNonNull(configurationService);
        this.ignoreStorage = Objects.requireNonNull(ignoreStorage);
        this.playerLangStorage = Objects.requireNonNull(playerLangStorage);
        this.playerChannelStorage = Objects.requireNonNull(playerChannelStorage);
        this.channelManager = Objects.requireNonNull(channelManager);
        this.ignoreManager = Objects.requireNonNull(ignoreManager);
        this.spyManager = Objects.requireNonNull(spyManager);
        this.messageHistory = Objects.requireNonNull(messageHistory);
        this.offlineMessageStore = Objects.requireNonNull(offlineMessageStore);
        this.playerLangManager = Objects.requireNonNull(playerLangManager);
        this.languageManager = Objects.requireNonNull(languageManager);
        this.autoBroadcastManager = Objects.requireNonNull(autoBroadcastManager);
        this.chatLogger = Objects.requireNonNull(chatLogger);
        this.dndManager = Objects.requireNonNull(dndManager);
        this.slowmodeManager = Objects.requireNonNull(slowmodeManager);
        this.chatLockManager = Objects.requireNonNull(chatLockManager);
        this.mentionManager = Objects.requireNonNull(mentionManager);
        this.staffChatManager = Objects.requireNonNull(staffChatManager);
        this.chatReplay = Objects.requireNonNull(chatReplay);
        this.afkManager = Objects.requireNonNull(afkManager);
        this.commandRegistry = Objects.requireNonNull(commandRegistry);
        this.listenerRegistry = Objects.requireNonNull(listenerRegistry);
        this.serviceRegistry = Objects.requireNonNull(serviceRegistry);
        this.managerRegistry = Objects.requireNonNull(managerRegistry);
        this.placeholderExpansion = Objects.requireNonNull(placeholderExpansion);
        this.updateChecker = Objects.requireNonNull(updateChecker);
        this.discordSrvEnabled = discordSrvEnabled;
    }
}
