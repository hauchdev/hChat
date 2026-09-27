package dev.hauch.hchat;

import dev.faststats.bukkit.BukkitContext;
import dev.hauch.hchat.bootstrap.Bootstrap;
import org.bukkit.plugin.java.JavaPlugin;

// class HChat
public final class HChat extends JavaPlugin {

    /** Project token from https://faststats.dev - metrics are always on. */
    private static final String FASTSTATS_TOKEN = "6eb22d8e72f50c81507087a3596bbf6d";

    private BukkitContext fastStatsContext;

    @Override
    // on enable
    public void onEnable() {
        Bootstrap.initialize(this);
        startMetrics();
    }

    // start faststats.dev metrics
    private void startMetrics() {
        try {
            fastStatsContext = new BukkitContext.Factory(this, FASTSTATS_TOKEN).create();
            fastStatsContext.ready();
            getLogger().info("faststats.dev metrics enabled.");
        } catch (Throwable t) {
            // broken metrics must never take the chat plugin down
            getLogger().warning("faststats.dev metrics failed to start: " + t.getMessage());
            fastStatsContext = null;
        }
    }

    @Override
    // on disable
    public void onDisable() {
        if (fastStatsContext != null) {
            try {
                fastStatsContext.shutdown();
            } catch (Throwable t) {
                getLogger().warning("faststats.dev shutdown raised: " + t.getMessage());
            }
            fastStatsContext = null;
        }
        Bootstrap.shutdown();
    }

    // reload plugin
    public void reloadPlugin() {
        Bootstrap.reload();
    }
}
