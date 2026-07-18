package dev.hauch.hChat.listeners;

import dev.hauch.hChat.HChat;
import dev.hauch.hChat.utils.MessageFormatter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final HChat plugin;

    public PlayerJoinListener(HChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        java.util.List<String> pending = plugin.getOfflineMessageStore()
                .drain(player.getUniqueId());
        if (pending.isEmpty()) return;

        player.sendMessage(MessageFormatter.format(
                plugin.getMessages().getString("offline-message-delivered")));
        for (String rendered : pending) {
            // rendered already serialized via PlainTextComponentSerializer when stored.
            player.sendMessage(net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson()
                    .deserialize(rendered));
        }

        if (plugin.getConfigManager().isOfflineSoundEnabled()) {
            try {
                Sound sound = Sound.valueOf(plugin.getConfigManager().getOfflineSound().toUpperCase());
                player.playSound(player.getLocation(), sound,
                        plugin.getConfigManager().getOfflineSoundVolume(),
                        plugin.getConfigManager().getOfflineSoundPitch());
            } catch (IllegalArgumentException ignored) {}
        }
    }
}