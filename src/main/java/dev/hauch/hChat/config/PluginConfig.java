package dev.hauch.hChat.config;

import dev.hauch.hChat.HChat;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PluginConfig {

    private final HChat plugin;
    private FileConfiguration config;

    public PluginConfig(HChat plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        this.language = config.getString("lang", "en");
        this.debug = config.getBoolean("debug", false);

    }

    private boolean debug;
    private String language;

    @NotNull
    public String getLanguage() {
        return language;
    }

    public boolean isDebug() {
        return debug;
    }


    public boolean isMentionSoundEnabled() {
        return config.getBoolean("mentions.sound.enabled", true);
    }

    public String getMentionSound() {
        return config.getString("mentions.sound.sound", "entity.experience_orb.pickup");
    }

    public float getMentionSoundVolume() {
        return (float) config.getDouble("mentions.sound.volume", 1.0);
    }

    public float getMentionSoundPitch() {
        return (float) config.getDouble("mentions.sound.pitch", 1.0);
    }

    public boolean isMentionColorsEnabled() {
        return config.getBoolean("mentions.colors.enabled", true);
    }

    public String getMentionColor() {
        return config.getString("mentions.colors.color", "&#54A3FF");
    }

    public String getMessageSenderFormat() {
        return config.getString("direct-messages.sender.format",
                "&7[&eYou &7-> &e{receiver}&7] &7{message}");
    }

    public boolean isMessageSenderHoverTextEnabled() {
        return config.getBoolean("direct-messages.sender.hover-text.enabled", false);
    }

    public List<String> getMessageSenderHoverText() {
        return config.getStringList("direct-messages.sender.hover-text.text");
    }

    public String getMessageReceiverFormat() {
        return config.getString("direct-messages.receiver.format",
                "&7[&e{sender} &7-> &eYou&7] &7{message}");
    }

    public boolean isMessageReceiverHoverTextEnabled() {
        return config.getBoolean("direct-messages.receiver.hover-text.enabled", true);
    }

    public List<String> getMessageReceiverHoverText() {
        return config.getStringList("direct-messages.receiver.hover-text.text");
    }

    public boolean isMessagesClickableActionsEnabled() {
        return config.getBoolean("direct-messages.clickable-actions.enabled", true);
    }

    public String getMessagesClickableActionsReplyCommand() {
        return config.getString("direct-messages.clickable-actions.reply-command",
                "/msg {sender}");
    }

    public String getSpyFormat() {
        return config.getString("spy-format",
                "&e{sender} &7-> &e{target}&7: &7{message}");
    }

    public boolean isHoverTextEnabled() {
        return config.getBoolean("hover-text.enabled", true);
    }

    public List<String> getHoverTextFormat() {
        return config.getStringList("hover-text.format");
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        this.language = config.getString("lang", "en");
        this.debug = config.getBoolean("debug", false);

    }

    public String getString(String path) {
        return config.getString(path);
    }

    public String getString(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public int getInt(String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public void set(String path, Object value) {
        config.set(path, value);
        plugin.saveConfig();
    }

    public boolean contains(String path) {
        return config.contains(path);
    }

    public ConfigurationSection getConfigurationSection(String path) {
        return config.getConfigurationSection(path);
    }

    public Set<String> getKeys(boolean deep) {
        return config.getKeys(deep);
    }

    public boolean isBroadcastEnabled() {
        return config.getBoolean("broadcast.enabled", true);
    }

    public List<String> getBroadcastFormat() {
        return config.getStringList("broadcast.format");
    }

    public Map<String, Map<String, Object>> getAutoBroadcasts() {
        Map<String, Map<String, Object>> result = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("broadcast");
        if (section == null) return result;

        for (String key : section.getKeys(false)) {
            ConfigurationSection sub = section.getConfigurationSection(key);
            if (sub == null) continue;
            if (!sub.contains("message") || !sub.contains("interval")) continue;

            Map<String, Object> data = new HashMap<>();
            data.put("enabled", sub.getBoolean("enabled", true));
            data.put("message", sub.getString("message", ""));
            data.put("interval", sub.getInt("interval", 600));
            data.put("display-in-console", sub.getBoolean("display-in-console", true));
            result.put(key, data);
        }
        return result;
    }

    public boolean isPrivateMessageLoggingEnabled() {
        return config.getBoolean("logging.private-messages.enabled", true);
    }

    public String getLoggingPath() {
        return config.getString("logging.private-messages.path", "logs");
    }

    public int getLoggingRetentionDays() {
        return config.getInt("logging.private-messages.retention-days", 30);
    }

    public int getOfflineMessagesMaxPending() {
        return config.getInt("offline-messages.max-pending", 10);
    }

    public boolean isOfflineSoundEnabled() {
        return config.getBoolean("offline-messages.sound.enabled", true);
    }

    public String getOfflineSound() {
        return config.getString("offline-messages.sound.sound", "entity.experience_orb.pickup");
    }

    public float getOfflineSoundVolume() {
        return (float) config.getDouble("offline-messages.sound.volume", 1.0);
    }

    public float getOfflineSoundPitch() {
        return (float) config.getDouble("offline-messages.sound.pitch", 1.0);
    }

    public boolean isWordFilterEnabled() {
        return config.getBoolean("word-filter.enabled", false);
    }

    public List<String> getWordFilterWords() {
        return config.getStringList("word-filter.words");
    }

    public String getWordFilterAction() {
        return config.getString("word-filter.action", "block");
    }
}