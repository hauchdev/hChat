package dev.hauch.hchat.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// class PluginConfig
public class PluginConfig {

    /**
     * Bump this value whenever config.yml gains new keys. On the next
     * startup (or /hchat reload) the missing keys are merged from the
     * bundled default file, so users never have to delete config.yml.
     */
    private static final int CONFIG_VERSION = 4;

    private final Plugin plugin;
    private FileConfiguration config;

    private boolean debug;
    private String language;

    // make PluginConfig
    public PluginConfig(Plugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    // load config
    public final void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        migrateConfig();

        this.language = config.getString("lang", "en");
        this.debug = config.getBoolean("debug", false);
    }

    /**
     * Adds every key that exists in the bundled default config.yml but is
     * missing from the on-disk file. Existing user values are preserved.
     * The file is only rewritten when something actually changed, so
     * comments and formatting survive migrations that add nothing.
     */
    private void migrateConfig() {
        int storedVersion = config.getInt("config-version", 0);
        if (storedVersion >= CONFIG_VERSION) return;

        FileConfiguration defaults = loadDefaultConfig();
        if (defaults == null) return;

        boolean changed = false;
        for (String key : defaults.getKeys(true)) {
            boolean defaultIsSection = defaults.isConfigurationSection(key);
            if (!config.contains(key)) {
                if (defaultIsSection) continue; // created implicitly by children
                config.set(key, defaults.get(key));
                changed = true;
            } else if (config.isConfigurationSection(key) != defaultIsSection) {
                // type conflict: the user value cannot host the new
                // options, so the default takes over for this path
                config.set(key, defaults.get(key));
                changed = true;
            }
        }

        config.set("config-version", CONFIG_VERSION);
        if (!changed) return; // nothing new - keep the file untouched

        try {
            config.save(new File(plugin.getDataFolder(), "config.yml"));
            plugin.getLogger().info("[hChat] Config migrated to v" + CONFIG_VERSION
                    + " - new options added, your settings were kept.");
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save migrated config: "
                    + e.getMessage());
        }
    }

    // load the default config.yml bundled inside the jar
    private FileConfiguration loadDefaultConfig() {
        try (InputStream in = plugin.getResource("config.yml")) {
            if (in == null) return null;
            return YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to read bundled config.yml: "
                    + e.getMessage());
            return null;
        }
    }

    @NotNull
    // get language
    public String getLanguage() {
        return language;
    }

    // is debugged
    public boolean isDebug() {
        return debug;
    }

    // is metrics enabled
    public boolean isMetricsEnabled() {
        return config.getBoolean("metrics.enabled", true);
    }

    // is mention sound enabled
    public boolean isMentionSoundEnabled() {
        return config.getBoolean("mentions.sound.enabled", true);
    }

    // get mention sound
    public String getMentionSound() {
        return config.getString("mentions.sound.sound", "entity.experience_orb.pickup");
    }

    // get mention sound volume
    public float getMentionSoundVolume() {
        return (float) config.getDouble("mentions.sound.volume", 1.0);
    }

    // get mention sound pitch
    public float getMentionSoundPitch() {
        return (float) config.getDouble("mentions.sound.pitch", 1.0);
    }

    // is mention colors enabled
    public boolean isMentionColorsEnabled() {
        return config.getBoolean("mentions.colors.enabled", true);
    }

    // get mention color
    public String getMentionColor() {
        return config.getString("mentions.colors.color", "&#54A3FF");
    }

    // get message sender format
    public String getMessageSenderFormat() {
        return config.getString("direct-messages.sender.format",
                "&7[&eYou &7-> &e{receiver}&7] &7{message}");
    }

    // is message sender hover text enabled
    public boolean isMessageSenderHoverTextEnabled() {
        return config.getBoolean("direct-messages.sender.hover-text.enabled", false);
    }

    // get message sender hover text
    public List<String> getMessageSenderHoverText() {
        return config.getStringList("direct-messages.sender.hover-text.text");
    }

    // get message receiver format
    public String getMessageReceiverFormat() {
        return config.getString("direct-messages.receiver.format",
                "&7[&e{sender} &7-> &eYou&7] &7{message}");
    }

    // is message receiver hover text enabled
    public boolean isMessageReceiverHoverTextEnabled() {
        return config.getBoolean("direct-messages.receiver.hover-text.enabled", true);
    }

    // get message receiver hover text
    public List<String> getMessageReceiverHoverText() {
        return config.getStringList("direct-messages.receiver.hover-text.text");
    }

    // is messages clickable actions enabled
    public boolean isMessagesClickableActionsEnabled() {
        return config.getBoolean("direct-messages.clickable-actions.enabled", true);
    }

    // get messages clickable actions reply command
    public String getMessagesClickableActionsReplyCommand() {
        return config.getString("direct-messages.clickable-actions.reply-command",
                "/msg {sender}");
    }

    // get spy format
    public String getSpyFormat() {
        return config.getString("spy-format",
                "&e{sender} &7-> &e{target}&7: &7{message}");
    }

    // is hover text enabled
    public boolean isHoverTextEnabled() {
        return config.getBoolean("hover-text.enabled", true);
    }

    // get hover text format
    public List<String> getHoverTextFormat() {
        return config.getStringList("hover-text.format");
    }

    // reload config
    public void reloadConfig() {
        loadConfig();
    }

    // get string
    public String getString(String path) {
        return config.getString(path);
    }

    // get string
    public String getString(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }

    // get int
    public int getInt(String path) {
        return config.getInt(path);
    }

    // get int
    public int getInt(String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }

    // get boolean
    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    // get boolean
    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }

    // get string list
    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    // set data
    public void set(String path, Object value) {
        config.set(path, value);
        plugin.saveConfig();
    }

    // contains data
    public boolean contains(String path) {
        return config.contains(path);
    }

    // get configuration section
    public ConfigurationSection getConfigurationSection(String path) {
        return config.getConfigurationSection(path);
    }

    // get keys
    public Set<String> getKeys(boolean deep) {
        return config.getKeys(deep);
    }

    // is broadcast enabled
    public boolean isBroadcastEnabled() {
        return config.getBoolean("broadcast.enabled", true);
    }

    // get broadcast cooldown ms
    public long getBroadcastCooldownMs() {
        return config.getLong("broadcast.cooldown-ms", 0);
    }

    // get broadcast format
    public List<String> getBroadcastFormat() {
        return config.getStringList("broadcast.format");
    }

    // get auto broadcasts
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

    // is private message logging enabled
    public boolean isPrivateMessageLoggingEnabled() {
        return config.getBoolean("logging.private-messages.enabled", true);
    }

    // get logging path
    public String getLoggingPath() {
        return config.getString("logging.private-messages.path", "logs");
    }

    // get logging retention days
    public int getLoggingRetentionDays() {
        return config.getInt("logging.private-messages.retention-days", 30);
    }

    // get offline messages max pending
    public int getOfflineMessagesMaxPending() {
        return config.getInt("offline-messages.max-pending", 10);
    }

    // is offline sound enabled
    public boolean isOfflineSoundEnabled() {
        return config.getBoolean("offline-messages.sound.enabled", true);
    }

    // get offline sound
    public String getOfflineSound() {
        return config.getString("offline-messages.sound.sound", "entity.experience_orb.pickup");
    }

    // get offline sound volume
    public float getOfflineSoundVolume() {
        return (float) config.getDouble("offline-messages.sound.volume", 1.0);
    }

    // get offline sound pitch
    public float getOfflineSoundPitch() {
        return (float) config.getDouble("offline-messages.sound.pitch", 1.0);
    }

    // is word filter enabled
    public boolean isWordFilterEnabled() {
        return config.getBoolean("word-filter.enabled", false);
    }

    // get word filter words
    public List<String> getWordFilterWords() {
        return config.getStringList("word-filter.words");
    }

    // get word filter action
    public String getWordFilterAction() {
        return config.getString("word-filter.action", "block");
    }

    // get default chat format
    public String getDefaultChatFormat() {
        return config.getString("chat.default-format",
                "&7{prefix}{player}{suffix}&8: &f{message}");
    }

    // is chat hex colors
    public boolean isChatHexColors() {
        return config.getBoolean("chat.hex-colors", true);
    }

    // get chat formats
    public Map<String, Map<String, String>> getChatFormats() {
        Map<String, Map<String, String>> out = new LinkedHashMap<>();
        ConfigurationSection section = config.getConfigurationSection("chat.formats");
        if (section == null) return out;

        for (String key : section.getKeys(false)) {
            ConfigurationSection sub = section.getConfigurationSection(key);
            if (sub == null) continue;
            Map<String, String> data = new HashMap<>();
            data.put("permission", sub.getString("permission", ""));
            data.put("format", sub.getString("format", getDefaultChatFormat()));
            out.put(key, data);
        }
        return out;
    }

    // get chat format
    public String resolveChatFormat(Player player) {
        for (Map<String, String> entry : getChatFormats().values()) {
            String perm = entry.get("permission");
            if (perm != null && !perm.isEmpty() && player.hasPermission(perm)) {
                return entry.get("format");
            }
        }
        return getDefaultChatFormat();
    }

    // is dynamic placeholder enabled
    public boolean isPlaceholderEnabled(String name) {
        return config.getBoolean("placeholders." + name + ".enabled", true);
    }

    // get ping symbol
    public String getPingSymbol() {
        return config.getString("placeholders.ping.symbol", "▪");
    }

    // is ping value shown
    public boolean isPingValueShown() {
        return config.getBoolean("placeholders.ping.show-value", true);
    }

    // get ping value suffix
    public String getPingValueSuffix() {
        return config.getString("placeholders.ping.value-suffix", "ms");
    }

    // get ping good max
    public int getPingGoodMax() {
        return config.getInt("placeholders.ping.good-max", 80);
    }

    // get ping medium max
    public int getPingMediumMax() {
        return config.getInt("placeholders.ping.medium-max", 150);
    }

    // get ping good color
    public String getPingGoodColor() {
        return config.getString("placeholders.ping.good-color", "&a");
    }

    // get ping medium color
    public String getPingMediumColor() {
        return config.getString("placeholders.ping.medium-color", "&e");
    }

    // get ping bad color
    public String getPingBadColor() {
        return config.getString("placeholders.ping.bad-color", "&c");
    }

    // get ping value color
    public String getPingValueColor() {
        return config.getString("placeholders.ping.value-color", "&7");
    }

    // get coords format
    public String getCoordsFormat() {
        return config.getString("placeholders.coords.format", "&7({x}, {y}, {z})");
    }

    // get world format
    public String getWorldFormat() {
        return config.getString("placeholders.world.format", "&7{world}");
    }

    // get afk placeholder
    public String getAfkPlaceholder() {
        return config.getString("placeholders.afk.placeholder", "%essentials_afk%");
    }

    // get afk values
    public List<String> getAfkValues() {
        List<String> values = config.getStringList("placeholders.afk.afk-values");
        return values.isEmpty() ? List.of("yes", "true") : values;
    }

    // get afk format
    public String getAfkFormat() {
        return config.getString("placeholders.afk.format", "&c[AFK]&r ");
    }

    // is welcome enabled
    public boolean isWelcomeEnabled() {
        return config.getBoolean("welcome.enabled", true);
    }

    // get welcome message
    public String getWelcomeMessage() {
        return config.getString("welcome.message",
                "&aWelcome {player} to the server!");
    }

    // is welcome hide vanilla
    public boolean isWelcomeHideVanilla() {
        return config.getBoolean("welcome.hide-vanilla", true);
    }

    // is welcome motd enabled
    public boolean isWelcomeMotdEnabled() {
        return config.getBoolean("welcome.motd-on-join", false);
    }

    // get welcome motd
    public String getWelcomeMotd() {
        return config.getString("welcome.motd", "");
    }

    // is welcome sound enabled
    public boolean isWelcomeSoundEnabled() {
        return config.getBoolean("welcome.sound.enabled", true);
    }

    // get welcome sound
    public String getWelcomeSound() {
        return config.getString("welcome.sound.sound", "entity.player.levelup");
    }

    // get welcome sound volume
    public float getWelcomeSoundVolume() {
        return (float) config.getDouble("welcome.sound.volume", 0.6);
    }

    // get welcome sound pitch
    public float getWelcomeSoundPitch() {
        return (float) config.getDouble("welcome.sound.pitch", 1.0);
    }

    // is first join enabled
    public boolean isFirstJoinEnabled() {
        return config.getBoolean("welcome.first-join.enabled", false);
    }

    // get first join message
    public String getFirstJoinMessage() {
        return config.getString("welcome.first-join.message",
                "&e{player} &7joined for the first time!");
    }

    // is first join hide vanilla
    public boolean isFirstJoinHideVanilla() {
        return config.getBoolean("welcome.first-join.hide-vanilla", true);
    }

    // is quit enabled
    public boolean isQuitEnabled() {
        return config.getBoolean("quit.enabled", true);
    }

    // get quit message
    public String getQuitMessage() {
        return config.getString("quit.message", "&7{player} left the server.");
    }

    // is quit hide vanilla
    public boolean isQuitHideVanilla() {
        return config.getBoolean("quit.hide-vanilla", true);
    }

    // is quit sound enabled
    public boolean isQuitSoundEnabled() {
        return config.getBoolean("quit.sound.enabled", false);
    }

    // get channels
    public Map<String, Map<String, Object>> getChannels() {
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        ConfigurationSection section = config.getConfigurationSection("channels");
        if (section == null) return result;

        for (String key : section.getKeys(false)) {
            ConfigurationSection sub = section.getConfigurationSection(key);
            if (sub == null) continue;

            Map<String, Object> data = new HashMap<>();
            data.put("format", sub.getString("format"));
            data.put("range", sub.getInt("range", -1));
            data.put("action-bar-hint", sub.getString("action-bar-hint"));
            data.put("speak-permission", sub.getString("speak-permission"));
            data.put("see-permission", sub.getString("see-permission"));
            data.put("cooldown-ms", sub.getLong("cooldown-ms", 0));
            data.put("alias", sub.getString("alias"));
            result.put(key, data);
        }
        return result;
    }

    // is update checker enabled
    public boolean isUpdateCheckerEnabled() {
        return config.getBoolean("update-checker.enabled", true);
    }

    // notify admins on join
    public boolean isUpdateNotifyAdmins() {
        return config.getBoolean("update-checker.notify-admins", true);
    }
}