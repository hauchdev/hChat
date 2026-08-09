package dev.hauch.hchat.manager;

import dev.hauch.hchat.config.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks the AFK state of every online player.
 * <p>
 * A player becomes AFK either manually ({@code /afk}) or automatically
 * after {@code afk.auto-timeout-seconds} without any activity. Activity
 * (movement, chat, commands, interactions) resets the idle timer and,
 * when {@code afk.unset-on-activity} is enabled, leaves the AFK state.
 * The {@code [afk]} chat token is driven from this manager.
 */
public final class AfkManager {

    private final PluginConfig config;

    /** players currently AFK, with the moment they went AFK + optional reason */
    /** only refresh the idle timer at most this often (ms) per player */
    private static final long ACTIVITY_THROTTLE_MS = 5_000L;

    private final Map<UUID, AfkState> states = new ConcurrentHashMap<>();
    /** last activity timestamp per player (touched from async chat too) */
    private final Map<UUID, Long> lastActivity = new ConcurrentHashMap<>();

    private volatile BukkitTask autoCheckTask;

    /** A single AFK state entry. */
    public record AfkState(long since, String reason) {}

    // make AfkManager
    public AfkManager(PluginConfig config) {
        this.config = config;
    }

    // is the built-in AFK feature enabled
    public boolean isEnabled() {
        return config.isAfkEnabled();
    }

    // is the player AFK
    public boolean isAfk(Player player) {
        return states.containsKey(player.getUniqueId());
    }

    // is the player AFK
    public boolean isAfk(UUID uuid) {
        return states.containsKey(uuid);
    }

    // set or clear the AFK state
    public void setAfk(Player player, boolean afk, String reason) {
        UUID id = player.getUniqueId();
        if (afk) {
            states.put(id, new AfkState(System.currentTimeMillis(), reason));
        } else {
            states.remove(id);
            lastActivity.put(id, System.currentTimeMillis());
        }
    }

    // player activity: reset the idle timer and (optionally) leave AFK.
    // The write is throttled so high-frequency events (move/interact) do
    // not hammer the map on large servers.
    public void touch(Player player) {
        UUID id = player.getUniqueId();
        long now = System.currentTimeMillis();
        Long last = lastActivity.get(id);
        if (!isAfk(player) && last != null
                && now - last < ACTIVITY_THROTTLE_MS) {
            return;
        }
        lastActivity.put(id, now);
        if (config.isAfkUnsetOnActivity() && isAfk(player)) {
            setAfk(player, false, null);
        }
    }

    // start (or restart) the periodic auto-AFK scan. The task reads the
    // timeout live from config every tick, so a changed timeout applies on
    // reload; the interval itself only changes on restart or reload().
    public void startAutoCheck(Plugin plugin) {
        int interval = config.getAfkCheckIntervalSeconds();
        if (interval <= 0) return;
        if (autoCheckTask != null && !autoCheckTask.isCancelled()) return;
        autoCheckTask = plugin.getServer().getScheduler().runTaskTimer(
                plugin, this::autoCheck, interval * 20L, interval * 20L);
    }

    // reload: apply a changed check interval by restarting the task
    public void reload(Plugin plugin) {
        if (autoCheckTask != null) {
            autoCheckTask.cancel();
            autoCheckTask = null;
        }
        startAutoCheck(plugin);
    }

    // mark idle players as AFK
    private void autoCheck() {
        int timeout = config.getAfkAutoTimeoutSeconds();
        if (timeout <= 0 || !isEnabled()) return;
        long now = System.currentTimeMillis();
        long timeoutMs = timeout * 1000L;
        for (Player player : Bukkit.getOnlinePlayers()) {
            Long last = lastActivity.get(player.getUniqueId());
            // unknown player: give them a grace period until the next run
            if (last == null) {
                lastActivity.put(player.getUniqueId(), now);
                continue;
            }
            if (!isAfk(player) && now - last >= timeoutMs) {
                setAfk(player, true, null);
            }
        }
    }
    // forget a player on quit (safe on any thread)
    public void remove(Player player) {
        UUID id = player.getUniqueId();
        states.remove(id);
        lastActivity.remove(id);
    }
}
