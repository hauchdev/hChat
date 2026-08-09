package dev.hauch.hchat.listener;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.AfkManager;
import dev.hauch.hchat.utils.MessageFormatter;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Feeds {@link AfkManager} with activity signals (movement, chat,
 * commands, interactions) and cleans up the AFK state on quit. Joining
 * initializes the idle timer. When an AFK player starts chatting again
 * and {@code afk.notify-unset} is enabled, a message announces it.
 */
public final class AfkActivityListener implements Listener {

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;
    private final AfkManager afkManager;

    // make AfkActivityListener
    public AfkActivityListener(Plugin plugin,
                               PluginConfig config,
                               PluginMessages messages,
                               AfkManager afkManager) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.afkManager = afkManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    // on join: start the idle timer
    public void onJoin(PlayerJoinEvent event) {
        afkManager.touch(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    // on quit: forget the player
    public void onQuit(PlayerQuitEvent event) {
        afkManager.remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    // on move: only count movement that changes the block position, so
    // server-side jitter / AFK pools do not keep players "active"
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) return;
        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        afkManager.touch(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    // on interact
    public void onInteract(PlayerInteractEvent event) {
        afkManager.touch(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    // on command
    public void onCommand(PlayerCommandPreprocessEvent event) {
        afkManager.touch(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    // on chat: leaving AFK is worth announcing. MONITOR runs after
    // ChatListener (HIGHEST), so a message blocked by slowmode / chatlock
    // / filters never un-afks the player or announces it.
    public void onChat(AsyncChatEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        boolean wasAfk = afkManager.isAfk(player);
        afkManager.touch(player);
        if (wasAfk && config.isAfkNotifyUnset()) {
            Map<String, String> ph = new HashMap<>();
            ph.put("player", player.getName());
            Bukkit.getScheduler().runTask(plugin, () -> plugin.getServer()
                    .sendMessage(MessageFormatter.format(
                            messages.getString("afk-no-longer"), ph)));
        }
    }
}
