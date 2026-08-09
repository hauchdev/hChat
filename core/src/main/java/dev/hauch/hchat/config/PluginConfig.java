package dev.hauch.hchat.config;

import dev.hauch.hchat.utils.FilterAction;
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
import java.nio.file.Files;
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
    private static final int CONFIG_VERSION = 7;

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

        backupOldConfig(storedVersion);
        try {
            config.save(new File(plugin.getDataFolder(), "config.yml"));
            plugin.getLogger().info("[hChat] Config migrated to v" + CONFIG_VERSION
                    + " - new options added, your settings were kept.");
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save migrated config: "
                    + e.getMessage());
        }
    }

    // keep the pre-migration file around, once per source version, so an
    // admin can diff or roll back after an upgrade
    private void backupOldConfig(int storedVersion) {
        File current = new File(plugin.getDataFolder(), "config.yml");
        File backup = new File(plugin.getDataFolder(),
                "config-backup-v" + storedVersion + ".yml");
        if (backup.exists()) return;
        try {
            Files.copy(current.toPath(), backup.toPath());
            plugin.getLogger().info("[hChat] Backed up old config to "
                    + backup.getName());
        } catch (IOException e) {
            plugin.getLogger().warning("Could not back up old config: "
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

    // action bar shown to the sender when the DM was not delivered
    // because the receiver has DND or ignores them
    public boolean isDmSilencedActionBarEnabled() {
        return config.getBoolean("direct-messages.silenced-action-bar.enabled", true);
    }

    // get the silenced DM action bar text
    public String getDmSilencedActionBarText() {
        return config.getString("direct-messages.silenced-action-bar.text",
                "&e{receiver} is muted - the message was not delivered.");
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

    // is the built-in AFK feature enabled
    public boolean isAfkEnabled() {
        return config.getBoolean("afk.enabled", true);
    }

    // seconds of inactivity before a player is marked AFK automatically (0 = off)
    public int getAfkAutoTimeoutSeconds() {
        return config.getInt("afk.auto-timeout-seconds", 300);
    }

    // how often the auto-AFK check runs (seconds)
    public int getAfkCheckIntervalSeconds() {
        return config.getInt("afk.check-interval-seconds", 30);
    }

    // leave the AFK state on any activity (movement, chat, command...)
    public boolean isAfkUnsetOnActivity() {
        return config.getBoolean("afk.unset-on-activity", true);
    }

    // announce when an AFK player starts chatting again
    public boolean isAfkNotifyUnset() {
        return config.getBoolean("afk.notify-unset", true);
    }

    // is slowmode enabled
    public boolean isSlowmodeEnabled() {
        return config.getBoolean("slowmode.enabled", true);
    }

    // get slowmode seconds
    public int getSlowmodeSeconds() {
        return config.getInt("slowmode.seconds", 3);
    }

    // get slowmode bypass permission
    public String getSlowmodeBypassPermission() {
        return config.getString("slowmode.bypass-permission", "hchat.slowmode.bypass");
    }

    // is chat lock enabled
    public boolean isChatLockEnabled() {
        return config.getBoolean("chat-lock.enabled", true);
    }

    // get chat lock bypass permission
    public String getChatLockBypassPermission() {
        return config.getString("chat-lock.bypass-permission", "hchat.chatlock.bypass");
    }

    // get staff chat format
    public String getStaffChatFormat() {
        return config.getString("staff-chat.format", "&c[Staff] {player} &8» &f{message}");
    }

    // is staff chat logged to console
    public boolean isStaffChatLogToConsole() {
        return config.getBoolean("staff-chat.log-to-console", true);
    }

    // is death message enabled
    public boolean isDeathMessageEnabled() {
        return config.getBoolean("death-message.enabled", true);
    }

    // get death message format
    public String getDeathMessageFormat() {
        return config.getString("death-message.format",
                "&c☠ &7{player} &fdied by {cause}");
    }

    // get death message custom
    public String getDeathMessageCustom(String cause) {
        return config.getString("death-message.custom." + cause);
    }

    // get everyone mention permission
    public String getEveryoneMentionPermission() {
        return config.getString("mentions.everyone-permission", "hchat.mention.everyone");
    }

    // get here mention permission
    public String getHereMentionPermission() {
        return config.getString("mentions.here-permission", "hchat.mention.here");
    }

    // get everyone mention cooldown seconds
    public int getEveryoneMentionCooldownSeconds() {
        return config.getInt("mentions.everyone-cooldown-seconds", 60);
    }

    // is everyone mention sound enabled
    public boolean isEveryoneMentionSoundEnabled() {
        return config.getBoolean("mentions.everyone-sound.enabled", true);
    }

    // get everyone mention sound
    public String getEveryoneMentionSound() {
        return config.getString("mentions.everyone-sound.sound", "entity.wither.spawn");
    }

    // get everyone mention sound volume
    public float getEveryoneMentionSoundVolume() {
        return (float) config.getDouble("mentions.everyone-sound.volume", 1.0);
    }

    // get everyone mention sound pitch
    public float getEveryoneMentionSoundPitch() {
        return (float) config.getDouble("mentions.everyone-sound.pitch", 1.0);
    }

    // bold highlight for mentioned names. A real background color is not
    // possible in vanilla chat (Adventure Style has no background), so the
    // highlight is rendered as bold text on top of the mention color.
    public boolean isMentionHighlightEnabled() {
        return config.getBoolean("mentions.highlight", true);
    }

    // is anti-caps enabled
    public boolean isAntiCapsEnabled() {
        return config.getBoolean("filters.anti-caps.enabled", true);
    }

    // get anti-caps max percent
    public int getAntiCapsMaxPercent() {
        return config.getInt("filters.anti-caps.max-uppercase-percent", 70);
    }

    // get anti-caps min length
    public int getAntiCapsMinLength() {
        return config.getInt("filters.anti-caps.min-length", 8);
    }

    // get anti-caps action
    public FilterAction getAntiCapsAction() {
        return FilterAction.parse(config.getString("filters.anti-caps.action", "mask"),
                FilterAction.MASK);
    }

    // is anti-unicode enabled
    public boolean isAntiUnicodeEnabled() {
        return config.getBoolean("filters.anti-unicode.enabled", true);
    }

    // is anti-unicode block invisible enabled
    public boolean isAntiUnicodeBlockInvisible() {
        return config.getBoolean("filters.anti-unicode.block-invisible", true);
    }

    // get anti-unicode action
    public FilterAction getAntiUnicodeAction() {
        return FilterAction.parse(config.getString("filters.anti-unicode.action", "block"),
                FilterAction.BLOCK);
    }

    // is anti-ad enabled
    public boolean isAntiAdEnabled() {
        return config.getBoolean("filters.anti-ad.enabled", true);
    }

    // get anti-ad patterns. When the key is absent the built-in defaults
    // apply; an explicit (even empty) list is respected as-is.
    public List<String> getAntiAdPatterns() {
        if (!config.contains("filters.anti-ad.patterns")) {
            // "me" is intentionally omitted: "help.me" / "give.me" would
            // false-positive on normal prose.
            return List.of(
                    "(?<![a-zA-Z0-9])([a-z0-9-]+\\.)+(com|net|org|gg|io)",
                    "(discord\\.gg/|discordapp\\.com/invite/)");
        }
        return config.getStringList("filters.anti-ad.patterns");
    }

    // get anti-ad whitelist
    public List<String> getAntiAdWhitelist() {
        return config.getStringList("filters.anti-ad.whitelist");
    }

    // get anti-ad action
    public FilterAction getAntiAdAction() {
        return FilterAction.parse(config.getString("filters.anti-ad.action", "warn"),
                FilterAction.WARN);
    }

    // is anti-spam enabled
    public boolean isAntiSpamEnabled() {
        return config.getBoolean("filters.anti-spam.enabled", true);
    }

    // get anti-spam global cooldown ms
    public long getAntiSpamGlobalCooldownMs() {
        return config.getLong("filters.anti-spam.global-cooldown-ms", 1500);
    }

    // get anti-spam flood max messages
    public int getAntiSpamFloodMaxMessages() {
        return config.getInt("filters.anti-spam.flood.max-messages", 5);
    }

    // get anti-spam flood window ms
    public long getAntiSpamFloodWindowMs() {
        return config.getLong("filters.anti-spam.flood.window-seconds", 3) * 1000L;
    }

    // get anti-spam flood similarity threshold
    public double getAntiSpamFloodSimilarity() {
        return config.getDouble("filters.anti-spam.flood.similarity-threshold", 0.8);
    }

    // get anti-spam action
    public FilterAction getAntiSpamAction() {
        return FilterAction.parse(config.getString("filters.anti-spam.action", "block"),
                FilterAction.BLOCK);
    }

    // get replay capacity
    public int getReplayCapacity() {
        return config.getInt("replay.capacity", 50);
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

    // get channels. Non-channel keys that live in the same section
    // (default-by-permission, hint flags) are skipped here.
    public Map<String, Map<String, Object>> getChannels() {
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        ConfigurationSection section = config.getConfigurationSection("channels");
        if (section == null) return result;

        for (String key : section.getKeys(false)) {
            if (isChannelOptionKey(key)) continue;
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
            data.put("per-world", sub.getBoolean("per-world", false));
            result.put(key, data);
        }
        return result;
    }

    // keys inside the channels section that are options, not channels
    private static boolean isChannelOptionKey(String key) {
        return "default-by-permission".equals(key)
                || "hint-on-switch".equals(key)
                || "hint-on-join".equals(key);
    }

    // get default channel per permission: permission -> channel id, in
    // config order (first match wins at join time)
    public Map<String, String> getDefaultChannelByPermission() {
        Map<String, String> result = new LinkedHashMap<>();
        List<?> list = config.getList("channels.default-by-permission");
        if (list == null) return result;
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> map)) continue;
            Object permission = map.get("permission");
            Object channel = map.get("channel");
            if (permission instanceof String p && !p.isBlank()
                    && channel instanceof String c && !c.isBlank()) {
                result.put(p, c);
            }
        }
        return result;
    }

    // show the active channel in the action bar after /channel
    public boolean isChannelHintOnSwitch() {
        return config.getBoolean("channels.hint-on-switch", true);
    }

    // show the active channel in the action bar on join
    public boolean isChannelHintOnJoin() {
        return config.getBoolean("channels.hint-on-join", true);
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