package dev.hauch.hChat;

import dev.hauch.hChat.commands.*;
import dev.hauch.hChat.config.PluginConfig;
import dev.hauch.hChat.config.PluginMessages;
import dev.hauch.hChat.listeners.ChatListener;
import dev.hauch.hChat.listeners.PlayerJoinListener;
import dev.hauch.hChat.listeners.PlayerQuitListener;
import dev.hauch.hChat.managers.AutoBroadcastManager;
import dev.hauch.hChat.managers.DndManager;
import dev.hauch.hChat.managers.IgnoreManager;
import dev.hauch.hChat.managers.MessageHistory;
import dev.hauch.hChat.managers.OfflineMessageStore;
import dev.hauch.hChat.managers.PlayerLangManager;
import dev.hauch.hChat.managers.SpyManager;
import dev.hauch.hChat.placeholders.HChatPlaceholderExpansion;
import dev.hauch.hChat.storage.PlayerLangStorage;
import dev.hauch.hChat.storage.YamlIgnoreStorage;
import dev.hauch.hChat.utils.ChatLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class HChat extends JavaPlugin {

    private static final Logger LOGGER = Bukkit.getLogger();
    private static HChat instance;

    // ─── config / lang ───
    private PluginConfig configManager;
    private PluginMessages messages;

    // ─── managers ───
    private IgnoreManager ignoreManager;
    private SpyManager spyManager;
    private MessageHistory messageHistory;
    private AutoBroadcastManager autoBroadcastManager;
    private OfflineMessageStore offlineMessageStore;
    private PlayerLangStorage playerLangStorage;
    private PlayerLangManager playerLangManager;
    private ChatLogger chatLogger;
    private DndManager dndManager;

    // ─── hooks ───
    private boolean discordSRVEnabled;

    public static HChat getInstance() { return instance; }

    public PluginConfig getConfigManager() { return configManager; }
    public PluginMessages getMessages() { return messages; }
    public IgnoreManager getIgnoreManager() { return ignoreManager; }
    public SpyManager getSpyManager() { return spyManager; }
    public MessageHistory getMessageHistory() { return messageHistory; }
    public AutoBroadcastManager getAutoBroadcastManager() { return autoBroadcastManager; }
    public OfflineMessageStore getOfflineMessageStore() { return offlineMessageStore; }
    public PlayerLangStorage getPlayerLangStorage() { return playerLangStorage; }
    public PlayerLangManager getPlayerLangManager() { return playerLangManager; }
    public ChatLogger getChatLogger() { return chatLogger; }
    public DndManager getDndManager() { return dndManager; }
    public boolean isDiscordSRVEnabled() { return discordSRVEnabled; }

    @Override
    public void onEnable() {
        instance = this;

        // ─── CONFIG ───
        this.configManager = new PluginConfig(this);
        this.messages = new PluginMessages(this);

        // ─── STORAGE ───
        YamlIgnoreStorage ignoreStorage = new YamlIgnoreStorage(this);
        this.playerLangStorage = new PlayerLangStorage(this);

        // ─── MANAGERS ───
        this.ignoreManager = new IgnoreManager(ignoreStorage);
        this.spyManager = new SpyManager();
        this.messageHistory = new MessageHistory();
        this.offlineMessageStore = new OfflineMessageStore(
                configManager.getOfflineMessagesMaxPending());
        this.playerLangManager = new PlayerLangManager(playerLangStorage);
        this.chatLogger = new ChatLogger(this);
        this.autoBroadcastManager = new AutoBroadcastManager(this);
        this.autoBroadcastManager.reload();
        this.chatLogger.purgeOldLogs();
        this.dndManager = new DndManager();

        // ─── COMMANDS ───
        registerCommands();

        // ─── LISTENERS ───
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);

        // ─── HOOKS ───
        this.discordSRVEnabled = Bukkit.getPluginManager().getPlugin("DiscordSRV") != null;
        if (discordSRVEnabled) {
            LOGGER.info("DiscordSRV detected — bridge available for Fase 5.");
        } else {
            LOGGER.info("DiscordSRV not found — running without Discord bridge.");
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new HChatPlaceholderExpansion(this).register();
            LOGGER.info("PlaceholderAPI expansion registered!");
        } else {
            LOGGER.info("PlaceholderAPI not found — placeholders disabled.");
        }

        LOGGER.info("hChat v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        if (autoBroadcastManager != null) autoBroadcastManager.shutdown();
        if (chatLogger != null) chatLogger.purgeOldLogs();
        LOGGER.info("hChat disabled!");
        instance = null;
    }

    private void registerCommands() {
        getCommand("message").setExecutor(new MessageCommand(this));
        getCommand("message").setTabCompleter(new MessageCommand(this));
        getCommand("reply").setExecutor(new ReplyCommand(this));
        getCommand("hchat").setExecutor(new HChatCommand(this));
        getCommand("clear").setExecutor(new ClearCommand(this));
        getCommand("ignore").setExecutor(new IgnoreCommand(this));
        getCommand("spy").setExecutor(new SpyCommand(this));
        getCommand("broadcast").setExecutor(new BroadcastCommand(this));
        getCommand("broadcast").setTabCompleter(new BroadcastCommand(this));
        getCommand("ignore").setTabCompleter(new IgnoreCommand(this));
        getCommand("spy").setTabCompleter(new SpyCommand(this));
    }

    public void reloadPlugin() {
        configManager.reloadConfig();
        messages.reload();
        ignoreManager.reload();
        autoBroadcastManager.reload();
        playerLangManager.saveAll();
        LOGGER.info("hChat reloaded!");
    }
}