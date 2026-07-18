package dev.hauch.hChat;

import dev.hauch.hChat.commands.*;
import dev.hauch.hChat.config.PluginConfig;
import dev.hauch.hChat.config.PluginMessages;
import dev.hauch.hChat.listeners.ChatListener;
import dev.hauch.hChat.managers.IgnoreManager;
import dev.hauch.hChat.managers.MessageHistory;
import dev.hauch.hChat.managers.SpyManager;
import dev.hauch.hChat.placeholders.HChatPlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class HChat extends JavaPlugin {

    private static final Logger LOGGER = Bukkit.getLogger();
    private static HChat instance;

    // Managers
    private PluginConfig configManager;
    private PluginMessages messages;
    private IgnoreManager ignoreManager;
    private SpyManager spyManager;
    private MessageHistory messageHistory;

    public static HChat getInstance() {
        return instance;
    }

    public PluginConfig getConfigManager() {
        return configManager;
    }

    public PluginMessages getMessages() {
        return messages;
    }

    public IgnoreManager getIgnoreManager() {
        return ignoreManager;
    }

    public SpyManager getSpyManager() {
        return spyManager;
    }

    public MessageHistory getMessageHistory() {
        return messageHistory;
    }

    @Override
    public void onEnable() {
        instance = this;

        // ─── CONFIG ───
        this.configManager = new PluginConfig(this);
        this.messages = new PluginMessages(this);

        // ─── MANAGERS ───
        this.ignoreManager = new IgnoreManager();
        this.spyManager = new SpyManager();
        this.messageHistory = new MessageHistory();

        // ─── COMMANDS ───
        registerCommands();

        // ─── LISTENERS ───
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

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
    }

    public void reloadPlugin() {
        configManager.reloadConfig();
        messages.reload();
        LOGGER.info("hChat reloaded!");
    }
}