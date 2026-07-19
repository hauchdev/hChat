package dev.hauch.hChat.listeners;

import dev.hauch.hChat.HChat;
import dev.hauch.hChat.utils.MessageFormatter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayerQuitListener implements Listener {

    private final HChat plugin;

    public PlayerQuitListener(HChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getConfigManager().isQuitEnabled()) return;

        String template = plugin.getConfigManager().getQuitMessage();
        if (template == null || template.isEmpty()) return;

        if (plugin.getConfigManager().isQuitHideVanilla()) {
            event.quitMessage(null);
        }

        Map<String, String> ph = new HashMap<>();
        ph.put("player", player.getName());
        plugin.getServer().sendMessage(MessageFormatter.format(template, ph));
    }
}