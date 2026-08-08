package dev.hauch.hchat;

import dev.hauch.hchat.api.HChatProvider;
import dev.hauch.hchat.bootstrap.Bootstrap;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

// class HChat
public final class HChat extends JavaPlugin {

    private static final int BSTATS_PLUGIN_ID = 33232;

    @Override
    // on enable
    public void onEnable() {
        Bootstrap.initialize(this);
        startMetrics();
    }

    // start bStats metrics
    private void startMetrics() {
        if (BSTATS_PLUGIN_ID <= 0) {
            getLogger().info("bStats metrics disabled.");
            return;
        }
        try {
            if (!HChatProvider.get().config().isMetricsEnabled()) {
                return;
            }
            new Metrics(this, BSTATS_PLUGIN_ID);
            getLogger().info("bStats metrics enabled.");
        } catch (Exception t) {
            // a broken bStats must never take the chat plugin down
            getLogger().warning("bStats metrics failed to start: " + t.getMessage());
        }
    }

    @Override
    // on disable
    public void onDisable() {
        Bootstrap.shutdown();
    }

    // reload plugin
    public void reloadPlugin() {
        Bootstrap.reload();
    }
}
