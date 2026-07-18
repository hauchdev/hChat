package dev.hauch.hChat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class HChat extends JavaPlugin {

    private static final Logger LOGGER = Bukkit.getLogger();

    @Override
    public void onEnable() {
        LOGGER.info("hChat enabled");
    }

    @Override
    public void onDisable() {
        LOGGER.info("hChat disabled");
    }
}
