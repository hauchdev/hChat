package dev.hauch.hChat.managers;

import dev.hauch.hChat.HChat;
import dev.hauch.hChat.utils.BroadcastRenderer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutoBroadcastManager {

    private final HChat plugin;
    private final List<BukkitTask> tasks = new ArrayList<>();

    public AutoBroadcastManager(HChat plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        cancelAll();
        load();
    }

    private void cancelAll() {
        for (BukkitTask task : tasks) {
            try { task.cancel(); } catch (IllegalStateException ignored) {}
        }
        tasks.clear();
    }

    private void load() {
        Map<String, Map<String, Object>> entries = plugin.getConfigManager().getAutoBroadcasts();
        if (entries.isEmpty()) {
            plugin.getLogger().info("[AutoBroadcast] No entries found in config.yml.");
            return;
        }

        for (Map.Entry<String, Map<String, Object>> e : entries.entrySet()) {
            Map<String, Object> data = e.getValue();
            boolean enabled = (boolean) data.getOrDefault("enabled", true);
            if (!enabled) continue;

            String message = (String) data.getOrDefault("message", "");
            if (message.isEmpty()) {
                plugin.getLogger().warning("[AutoBroadcast] Entry '" + e.getKey()
                        + "' has empty message — skipping.");
                continue;
            }

            int interval = (int) data.getOrDefault("interval", 600);
            if (interval < 1) {
                plugin.getLogger().warning("[AutoBroadcast] Entry '" + e.getKey()
                        + "' has invalid interval " + interval + " — using 600.");
                interval = 600;
            }
            boolean displayInConsole = (boolean) data.getOrDefault("display-in-console", true);

            long ticks = interval * 20L;
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                BroadcastRenderer.broadcast(plugin, message);
                if (displayInConsole) {
                    plugin.getLogger().info("[AutoBroadcast:" + e.getKey() + "] " + message);
                }
            }, ticks, ticks);
            tasks.add(task);

            plugin.getLogger().info("[AutoBroadcast] Scheduled '" + e.getKey()
                    + "' every " + interval + "s.");
        }
    }

    public void shutdown() {
        cancelAll();
    }
}