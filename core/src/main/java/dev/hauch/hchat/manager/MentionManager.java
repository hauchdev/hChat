package dev.hauch.hchat.manager;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cooldown state for mass mentions ({@code @everyone} / {@code @here}).
 * Kept separate from {@link ChannelManager} so mention throttling is
 * independent from channel and broadcast cooldowns.
 */
public final class MentionManager {

    private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    // remaining ms for a token (0 = allowed)
    public long cooldownRemaining(Player player, String key, long cooldownMs) {
        if (cooldownMs <= 0) return 0;
        Map<String, Long> used = cooldowns.get(player.getUniqueId());
        if (used == null) return 0;
        Long last = used.get(key);
        if (last == null) return 0;
        long elapsed = System.currentTimeMillis() - last;
        return elapsed >= cooldownMs ? 0 : cooldownMs - elapsed;
    }

    // record a usage for a token
    public void record(Player player, String key) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>())
                .put(key, System.currentTimeMillis());
    }
}
