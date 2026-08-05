package dev.hauch.hchat.manager;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.model.ChatChannel;
import dev.hauch.hchat.storage.PlayerChannelStorage;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads the configured channels, resolves which channel a message belongs
 * to (alias prefix {@code #staff}, then the player's active channel, then
 * the default), enforces speak/see permissions and per-channel cooldowns,
 * and persists the player's active channel.
 */
public final class ChannelManager {

    private static final long HINT_THROTTLE_MS = 20_000L;

    private final PluginConfig config;
    private final PlayerChannelStorage storage;

    // channels is rebuilt on the main thread but read from async chat
    // handlers; it is replaced atomically (never mutated in place) so
    // async readers always see a consistent, ordered snapshot. The
    // per-player maps are touched from both threads, so they must be
    // safe for concurrent access.
    private volatile Map<String, ChatChannel> channels = Map.of();
    private final Map<UUID, String> activeChannels = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastHintShown = new ConcurrentHashMap<>();

    private volatile String defaultId = "global";

    // make ChannelManager
    public ChannelManager(PluginConfig config, PlayerChannelStorage storage) {
        this.config = config;
        this.storage = storage;
        if (storage != null) {
            activeChannels.putAll(storage.loadAll());
        }
        reload();
    }

    // reload data
    public void reload() {
        Map<String, ChatChannel> rebuilt = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : config.getChannels().entrySet()) {
            Map<String, Object> data = entry.getValue();
            rebuilt.put(entry.getKey().toLowerCase(Locale.ROOT), new ChatChannel(
                    entry.getKey(),
                    (String) data.get("format"),
                    (int) data.getOrDefault("range", -1),
                    (String) data.getOrDefault("action-bar-hint", null),
                    (String) data.getOrDefault("speak-permission", null),
                    (String) data.getOrDefault("see-permission", null),
                    ((Number) data.getOrDefault("cooldown-ms", 0L)).longValue(),
                    (String) data.getOrDefault("alias", null)));
        }

        // always keep a usable default channel
        if (rebuilt.isEmpty()) {
            rebuilt.put("global", new ChatChannel(
                    "global", null, -1, null, null, null, 0L, null));
        }
        defaultId = rebuilt.containsKey("global") ? "global"
                : rebuilt.keySet().iterator().next();
        channels = rebuilt; // atomic swap - async readers see the new snapshot
    }

    // get channel by id (case-insensitive)
    public ChatChannel get(String id) {
        if (id == null) return null;
        return channels.get(id.toLowerCase(Locale.ROOT));
    }

    // all channels (insertion order)
    public Collection<ChatChannel> all() {
        return Collections.unmodifiableCollection(channels.values());
    }

    // default channel
    public ChatChannel defaultChannel() {
        return channels.get(defaultId);
    }

    // is the default channel
    public boolean isDefault(ChatChannel channel) {
        return channel != null && channel.id().equalsIgnoreCase(defaultId);
    }

    // resolve which channel a message goes to
    public ChatChannel resolveChannel(Player player, String message) {
        // 1. explicit alias prefix, e.g. "#staff hola"
        for (ChatChannel channel : channels.values()) {
            if (channel.hasAlias() && matchesAlias(message, channel.alias())) {
                return channel;
            }
        }
        // 2. the player's active channel (if they can still see it)
        String activeId = activeChannels.get(player.getUniqueId());
        if (activeId != null) {
            ChatChannel active = channels.get(activeId.toLowerCase(Locale.ROOT));
            if (active != null && canSee(player, active)) {
                return active;
            }
        }
        // 3. fall back to the default channel
        return defaultChannel();
    }

    // strip the alias prefix from a message, or null when it does not
    // start with the alias or nothing is left after stripping
    public String stripAlias(String message, String alias) {
        if (!matchesAlias(message, alias)) return null;
        String stripped = message.substring(alias.length()).trim();
        return stripped.isEmpty() ? null : stripped;
    }

    // does the message start with the alias (case-insensitive, followed by whitespace/end)
    public boolean matchesAlias(String message, String alias) {
        if (message == null || alias == null || alias.isBlank()) return false;
        String m = message.toLowerCase(Locale.ROOT);
        String a = alias.toLowerCase(Locale.ROOT);
        if (!m.startsWith(a)) return false;
        if (m.length() == a.length()) return true;
        return Character.isWhitespace(m.charAt(a.length()));
    }

    // can the player speak in this channel
    public boolean canSpeak(Player player, ChatChannel channel) {
        if (!channel.hasSpeakPermission()) return true;
        return player.hasPermission(channel.speakPermission());
    }

    // can the player see messages of this channel
    public boolean canSee(Player player, ChatChannel channel) {
        if (!channel.hasSeePermission()) return true;
        return player.hasPermission(channel.seePermission());
    }

    // player's active channel (or default)
    public ChatChannel activeChannel(Player player) {
        String id = activeChannels.get(player.getUniqueId());
        if (id != null) {
            ChatChannel channel = channels.get(id.toLowerCase(Locale.ROOT));
            if (channel != null) return channel;
        }
        return defaultChannel();
    }

    // switch active channel (persisted); default id resets to the default channel
    public void setActiveChannel(Player player, ChatChannel channel) {
        if (channel == null || isDefault(channel)) {
            activeChannels.remove(player.getUniqueId());
            if (storage != null) storage.remove(player.getUniqueId());
            return;
        }
        activeChannels.put(player.getUniqueId(), channel.id());
        if (storage != null) storage.save(player.getUniqueId(), channel.id());
    }

    // remaining cooldown ms for a key, 0 when allowed (shared with broadcast)
    public long cooldownRemaining(Player player, String key, long cooldownMs) {
        if (cooldownMs <= 0) return 0;
        Map<String, Long> used = cooldowns.get(player.getUniqueId());
        if (used == null) return 0;
        Long last = used.get(key);
        if (last == null) return 0;
        long elapsed = System.currentTimeMillis() - last;
        return elapsed >= cooldownMs ? 0 : cooldownMs - elapsed;
    }

    // record a usage for cooldown purposes
    public void recordCooldown(Player player, String key) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>())
                .put(key, System.currentTimeMillis());
    }

    // true when an action-bar hint may be shown (rate-limited per player)
    public boolean shouldShowHint(Player player) {
        long now = System.currentTimeMillis();
        Long last = lastHintShown.get(player.getUniqueId());
        if (last != null && now - last < HINT_THROTTLE_MS) return false;
        lastHintShown.put(player.getUniqueId(), now);
        return true;
    }
}
