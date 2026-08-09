package dev.hauch.hchat.listener;

import dev.hauch.hchat.api.platform.SoundLookup;
import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.AfkManager;
import dev.hauch.hchat.manager.ChannelManager;
import dev.hauch.hchat.manager.OfflineMessageStore;
import dev.hauch.hchat.model.ChatChannel;
import dev.hauch.hchat.update.UpdateChecker;
import dev.hauch.hchat.utils.MessageFormatter;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// class PlayerJoinListener
public class PlayerJoinListener implements Listener {

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;
    private final OfflineMessageStore offlineStore;
    private final UpdateChecker updateChecker;
    private final ChannelManager channelManager;
    private final AfkManager afkManager;

    // make PlayerJoinListener
    public PlayerJoinListener(Plugin plugin,
                              PluginConfig config,
                              PluginMessages messages,
                              OfflineMessageStore offlineStore,
                              UpdateChecker updateChecker,
                              ChannelManager channelManager,
                              AfkManager afkManager) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.offlineStore = offlineStore;
        this.updateChecker = updateChecker;
        this.channelManager = channelManager;
        this.afkManager = afkManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    // on join
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // default channel by permission (never overrides a persisted choice)
        channelManager.applyDefaultByPermission(player);

        if (config.isChannelHintOnJoin()) {
            ChatChannel active = channelManager.activeChannel(player);
            Component hint = active.hasActionBarHint()
                    ? MessageFormatter.format(active.actionBarHint())
                    : MessageFormatter.format(messages.getString("channel-current"),
                            Map.of("channel", active.id()));
            player.sendActionBar(hint);
        }

        afkManager.touch(player);

        boolean firstJoin = !player.hasPlayedBefore();

        String welcomeTemplate = firstJoin
                ? (config.isFirstJoinEnabled() ? config.getFirstJoinMessage() : null)
                : (config.isWelcomeEnabled() ? config.getWelcomeMessage() : null);

        if (welcomeTemplate != null && !welcomeTemplate.isEmpty()) {
            if (firstJoin && config.isFirstJoinHideVanilla()) {
                event.joinMessage(null);
            } else if (!firstJoin && config.isWelcomeHideVanilla()) {
                event.joinMessage(null);
            }
        }

        List<OfflineMessageStore.OfflineMessage> pending =
                offlineStore.peek(player.getUniqueId());
        if (!pending.isEmpty()) {
            Map<String, String> ph = new HashMap<>();
            ph.put("count", String.valueOf(pending.size()));
            player.sendMessage(MessageFormatter.withSuggestCommand(
                    MessageFormatter.format(
                            messages.getString("offline-messages-pending"), ph),
                    "/hchat mail read"));
            if (config.isOfflineSoundEnabled()) {
                playSound(player,
                        config.getOfflineSound(),
                        config.getOfflineSoundVolume(),
                        config.getOfflineSoundPitch());
            }
        }

        if (welcomeTemplate != null && !welcomeTemplate.isEmpty()) {
            Map<String, String> ph = new HashMap<>();
            ph.put("player", player.getName());
            plugin.getServer().sendMessage(MessageFormatter.format(welcomeTemplate, ph));
        }

        if (!firstJoin && config.isWelcomeMotdEnabled()) {
            String motd = config.getWelcomeMotd();
            if (motd != null && !motd.isEmpty()) {
                Map<String, String> ph = new HashMap<>();
                ph.put("player", player.getName());
                player.sendMessage(MessageFormatter.format(motd, ph));
            }
        }

        if (!firstJoin && config.isWelcomeSoundEnabled()) {
            playSound(player,
                    config.getWelcomeSound(),
                    config.getWelcomeSoundVolume(),
                    config.getWelcomeSoundPitch());
        }

        if (config.isUpdateNotifyAdmins()
                && updateChecker.isChecked()
                && updateChecker.isUpdateAvailable()
                && player.hasPermission("hchat.update")) {
            player.sendMessage(updateChecker.updateMessage(messages));
        }
    }

    // play sound
    private void playSound(Player p, String soundName, float volume, float pitch) {
        try {
            Sound sound = SoundLookup.resolve(soundName);
            p.playSound(p.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException ignored) { }
    }
}
