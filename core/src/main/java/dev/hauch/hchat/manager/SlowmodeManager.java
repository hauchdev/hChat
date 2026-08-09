package dev.hauch.hchat.manager;

import dev.hauch.hchat.config.PluginConfig;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global chat slowmode: after sending a message the player must wait
 * {@code seconds} before the next one. Players with the bypass permission
 * are exempt. The cooldown is per player, never global, so a slowmode of
 * N seconds means "at most one message per N seconds".
 * <p>
 * The duration is kept in memory ({@code /slowmode} never rewrites
 * config.yml, so user comments are preserved). It is initialised from the
 * config value at startup and re-synced on {@code /hchat reload}.
 */
public final class SlowmodeManager {

    private final PluginConfig config;
    private final Map<UUID, Long> lastSpoken = new ConcurrentHashMap<>();

    private volatile int seconds;

    // make SlowmodeManager
    public SlowmodeManager(PluginConfig config) {
        this.config = config;
        this.seconds = config.getSlowmodeSeconds();
    }

    // reload the configured duration
    public void reload() {
        this.seconds = config.getSlowmodeSeconds();
    }

    // current slowmode duration in seconds
    public int getSeconds() {
        return seconds;
    }

    // change the duration at runtime (in memory only)
    public void setSeconds(int seconds) {
        this.seconds = Math.max(0, seconds);
        reset();
    }

    // is slowmode active (enabled and a positive duration)
    public boolean isEnabled() {
        return config.isSlowmodeEnabled() && seconds > 0;
    }

    // remaining milliseconds before the player may speak again (0 = allowed)
    public long cooldownRemaining(Player player) {
        if (!isEnabled()) return 0;
        if (player.hasPermission(config.getSlowmodeBypassPermission())) return 0;
        Long last = lastSpoken.get(player.getUniqueId());
        if (last == null) return 0;
        long elapsed = System.currentTimeMillis() - last;
        long cooldownMs = seconds * 1000L;
        return elapsed >= cooldownMs ? 0 : cooldownMs - elapsed;
    }

    // record that the player just spoke
    public void record(Player player) {
        lastSpoken.put(player.getUniqueId(), System.currentTimeMillis());
    }

    // forget every player (e.g. slowmode turned off or changed)
    public void reset() {
        lastSpoken.clear();
    }
}
