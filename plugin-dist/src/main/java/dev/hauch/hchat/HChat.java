package dev.hauch.hchat;

import dev.hauch.hchat.bootstrap.Bootstrap;
import org.bukkit.plugin.java.JavaPlugin;

// class HChat
public final class HChat extends JavaPlugin {

    @Override
    // on enable
    public void onEnable() {
        Bootstrap.initialize(this);
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
