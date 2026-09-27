package dev.hauch.hchat;

import dev.faststats.bukkit.BukkitContext;
import dev.hauch.hchat.api.HChatProvider;
import dev.hauch.hchat.bootstrap.Bootstrap;
import org.bukkit.plugin.java.JavaPlugin;

// class HChat
public final class HChat extends JavaPlugin {

    /** Project token from https://faststats.dev - empty disables metrics. */
    private static final String FASTSTATS_TOKEN = "";

    private BukkitContext fastStatsContext;

    @Override
    // on enable
    public void onEnable() {
        Bootstrap.initialize(this);
        startMetrics();
    }

    // start faststats.dev metrics
    private void startMetrics() {
        String token = FASTSTATS_TOKEN;
        try {
            token = HChatProvider.get().config().getMetricsToken();
        } catch (Exception ignored) {
            // config not ready - fall back to the compiled-in token
        }
        if (token == null || token.isBlank()) {
            token = FASTSTATS_TOKEN;
        }
        if (token == null || token.isBlank()) {
            getLogger().info("faststats.dev metrics disabled (no token).");
            return;
        }
        try {
            fastStatsContext = new BukkitContext.Factory(this, token).create();
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
