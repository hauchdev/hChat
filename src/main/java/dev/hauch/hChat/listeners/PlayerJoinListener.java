package dev.hauch.hChat.listeners;

import dev.hauch.hChat.HChat;
import dev.hauch.hChat.utils.MessageFormatter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerJoinListener implements Listener {

    private final HChat plugin;

    public PlayerJoinListener(HChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        boolean firstJoin = !player.hasPlayedBefore();

        String welcomeTemplate = firstJoin
                ? (plugin.getConfigManager().isFirstJoinEnabled()
                ? plugin.getConfigManager().getFirstJoinMessage() : null)
                : (plugin.getConfigManager().isWelcomeEnabled()
                ? plugin.getConfigManager().getWelcomeMessage() : null);

        if (welcomeTemplate != null && !welcomeTemplate.isEmpty()) {
            if (firstJoin && plugin.getConfigManager().isFirstJoinHideVanilla()) {
                event.joinMessage(null);
            } else if (!firstJoin && plugin.getConfigManager().isWelcomeHideVanilla()) {
                event.joinMessage(null);
            }
        }

        List<String> pending = plugin.getOfflineMessageStore()
                .drain(player.getUniqueId());
        if (!pending.isEmpty()) {
            player.sendMessage(MessageFormatter.format(
                    plugin.getMessages().getString("offline-message-delivered")));
            for (String rendered : pending) {
                player.sendMessage(
                        net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson()
                                .deserialize(rendered));
            }
            if (plugin.getConfigManager().isOfflineSoundEnabled()) {
                playSound(player,
                        plugin.getConfigManager().getOfflineSound(),
                        plugin.getConfigManager().getOfflineSoundVolume(),
                        plugin.getConfigManager().getOfflineSoundPitch());
            }
        }

        if (welcomeTemplate != null && !welcomeTemplate.isEmpty()) {
            Map<String, String> ph = new HashMap<>();
            ph.put("player", player.getName());
            // Server#sendMessage(Component) es la API Adventure nativa
            // de Paper 1.20+: envía el componente a todos los jugadores
            // online preservando hex/&-colores.
            plugin.getServer().sendMessage(MessageFormatter.format(welcomeTemplate, ph));
        }

        if (!firstJoin && plugin.getConfigManager().isWelcomeMotdEnabled()) {
            String motd = plugin.getConfigManager().getWelcomeMotd();
            if (motd != null && !motd.isEmpty()) {
                Map<String, String> ph = new HashMap<>();
                ph.put("player", player.getName());
                player.sendMessage(MessageFormatter.format(motd, ph));
            }
        }

        if (!firstJoin && plugin.getConfigManager().isWelcomeSoundEnabled()) {
            playSound(player,
                    plugin.getConfigManager().getWelcomeSound(),
                    plugin.getConfigManager().getWelcomeSoundVolume(),
                    plugin.getConfigManager().getWelcomeSoundPitch());
        }
    }

    private void playSound(Player p, String soundName, float volume, float pitch) {
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            p.playSound(p.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException ignored) {}
    }
}