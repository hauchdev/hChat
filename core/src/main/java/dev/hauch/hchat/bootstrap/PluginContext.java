package dev.hauch.hchat.bootstrap;

import dev.hauch.hchat.api.platform.PlatformAdapter;
import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.AutoBroadcastManager;
import dev.hauch.hchat.manager.DndManager;
import dev.hauch.hchat.manager.ChannelManager;
import dev.hauch.hchat.manager.IgnoreManager;
import dev.hauch.hchat.manager.LanguageManager;
import dev.hauch.hchat.manager.MessageHistory;
import dev.hauch.hchat.manager.OfflineMessageStore;
import dev.hauch.hchat.manager.PlayerLangManager;
import dev.hauch.hchat.manager.SpyManager;
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

final class PluginContext {

    final Plugin plugin;
    final PluginConfig config;
    final PluginMessages messages;
    final ConfigurationService configurationService;

    final IgnoreStorage ignoreStorage;
    final PlayerLangStorage playerLangStorage;
    final PlayerChannelStorage playerChannelStorage;

    final ChannelManager channelManager;
    final IgnoreManager ignoreManager;
    final SpyManager spyManager;
    final MessageHistory messageHistory;
    final OfflineMessageStore offlineMessageStore;
    final PlayerLangManager playerLangManager;
    final LanguageManager languageManager;
    final AutoBroadcastManager autoBroadcastManager;
    final ChatLogger chatLogger;
    final DndManager dndManager;

    final CommandRegistry commandRegistry;
    final ListenerRegistry listenerRegistry;
    final ServiceRegistry serviceRegistry;
    final ManagerRegistry managerRegistry;

    final PlatformAdapter platformAdapter;

    final HChatPlaceholderExpansion placeholderExpansion;
    final UpdateChecker updateChecker;

    final boolean discordSrvEnabled;

    PluginContext(Plugin plugin,
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
        this.commandRegistry = Objects.requireNonNull(commandRegistry);
        this.listenerRegistry = Objects.requireNonNull(listenerRegistry);
        this.serviceRegistry = Objects.requireNonNull(serviceRegistry);
        this.managerRegistry = Objects.requireNonNull(managerRegistry);
        this.placeholderExpansion = Objects.requireNonNull(placeholderExpansion);
        this.updateChecker = Objects.requireNonNull(updateChecker);
        this.discordSrvEnabled = discordSrvEnabled;
    }
}
