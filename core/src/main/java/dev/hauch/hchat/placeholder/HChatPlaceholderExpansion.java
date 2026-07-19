package dev.hauch.hchat.placeholder;

import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.DndManager;
import dev.hauch.hchat.manager.IgnoreManager;
import dev.hauch.hchat.manager.MessageHistory;
import dev.hauch.hchat.manager.SpyManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// class HChatPlaceholderExpansion
public class HChatPlaceholderExpansion extends PlaceholderExpansion {

    private final Plugin plugin;
    private final PluginMessages messages;
    private final IgnoreManager ignoreManager;
    private final SpyManager spyManager;
    private final DndManager dndManager;
    private final MessageHistory messageHistory;

    // make HChatPlaceholderExpansion
    public HChatPlaceholderExpansion(Plugin plugin,
                                     PluginMessages messages,
                                     IgnoreManager ignoreManager,
                                     SpyManager spyManager,
                                     DndManager dndManager,
                                     MessageHistory messageHistory) {
        this.plugin = plugin;
        this.messages = messages;
        this.ignoreManager = ignoreManager;
        this.spyManager = spyManager;
        this.dndManager = dndManager;
        this.messageHistory = messageHistory;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "hchat";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Hauchdev";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    // persist data
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer == null) return null;
        String lower = params.toLowerCase();

        if (lower.startsWith("ignored_")) {
            String name = lower.substring("ignored_".length());
            if (!offlinePlayer.isOnline()) {
                return messages.getString("placeholders.ignored.false");
            }
            Player online = offlinePlayer.getPlayer();
            Player target = Bukkit.getPlayerExact(name);
            if (target == null) return messages.getString("placeholders.ignored.false");
            return messages.getString(
                    ignoreManager.isIgnored(online, target)
                            ? "placeholders.ignored.true"
                            : "placeholders.ignored.false");
        }

        return switch (lower) {
            case "spy_enabled" -> messages.getString(
                    (offlinePlayer.isOnline()
                            && spyManager.isSpy(offlinePlayer.getPlayer()))
                            ? "placeholders.spy.enabled"
                            : "placeholders.spy.disabled");

            case "ignored" -> messages.getString(
                    (offlinePlayer.isOnline()
                            && ignoreManager.hasIgnoredPlayers(offlinePlayer.getUniqueId()))
                            ? "placeholders.ignored.true"
                            : "placeholders.ignored.false");

            case "ignored_count" -> {
                if (!offlinePlayer.isOnline()) yield "0";
                yield String.valueOf(ignoreManager
                        .getIgnoredPlayers(offlinePlayer.getUniqueId()).size());
            }
            case "last_sender" -> {
                if (!offlinePlayer.isOnline()) yield "None";
                UUID last = messageHistory.getLastSender(offlinePlayer.getUniqueId());
                yield last == null ? "None" : Bukkit.getOfflinePlayer(last).getName();
            }
            case "last_message_part" -> {
                if (!offlinePlayer.isOnline()) yield "-";
                String last = messageHistory.getLastMessage(offlinePlayer.getUniqueId());
                if (last == null) yield "-";
                yield last.length() > 30 ? last.substring(0, 30) + "..." : last;
            }
            case "last_seen" -> {
                if (!offlinePlayer.isOnline()) yield messages.getString("placeholders.last_seen.none");
                long minutes = messageHistory.getMinutesSinceLastReceived(offlinePlayer.getUniqueId());
                if (minutes < 0) yield messages.getString("placeholders.last_seen.none");
                Map<String, String> ph = new HashMap<>();
                ph.put("minutes", String.valueOf(minutes));
                yield messages.getString("placeholders.last_seen.recent", ph);
            }
            case "dnd_enabled" -> (offlinePlayer.isOnline()
                    && dndManager != null
                    && dndManager.isDnd(offlinePlayer.getUniqueId()))
                    ? messages.getString("placeholders.dnd.enabled")
                    : messages.getString("placeholders.dnd.disabled");

            default -> null;
        };
    }
}
