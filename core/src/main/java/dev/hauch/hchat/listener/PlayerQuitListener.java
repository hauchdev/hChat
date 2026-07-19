package dev.hauch.hchat.listener;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.utils.MessageFormatter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

// class PlayerQuitListener
public class PlayerQuitListener implements Listener {

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;

    // make PlayerQuitListener
    public PlayerQuitListener(Plugin plugin,
                              PluginConfig config,
                              PluginMessages messages) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    // on quit
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!config.isQuitEnabled()) return;

        String template = config.getQuitMessage();
        if (template == null || template.isEmpty()) return;

        if (config.isQuitHideVanilla()) {
            event.quitMessage(null);
        }

        Map<String, String> ph = new HashMap<>();
        ph.put("player", player.getName());
        plugin.getServer().sendMessage(MessageFormatter.format(template, ph));
    }
}
