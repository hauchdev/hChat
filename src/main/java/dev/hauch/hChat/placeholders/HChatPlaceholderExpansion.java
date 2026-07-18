package dev.hauch.hChat.placeholders;

import dev.hauch.hChat.HChat;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class HChatPlaceholderExpansion extends PlaceholderExpansion {

    private final HChat plugin;

    public HChatPlaceholderExpansion(HChat plugin) {
        this.plugin = plugin;
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
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer == null) return null;
        String lower = params.toLowerCase();

        // ── Prefijo dinámico `ignored_<nombre>` ──
        if (lower.startsWith("ignored_")) {
            String name = lower.substring("ignored_".length());
            if (!offlinePlayer.isOnline()) {
                return plugin.getMessages().getString("placeholders.ignored.false");
            }
            Player online = offlinePlayer.getPlayer();
            Player target = Bukkit.getPlayerExact(name);
            if (target == null) return plugin.getMessages().getString("placeholders.ignored.false");
            return plugin.getMessages().getString(
                    plugin.getIgnoreManager().isIgnored(online, target)
                            ? "placeholders.ignored.true"
                            : "placeholders.ignored.false");
        }

        return switch (lower) {
            case "spy_enabled" -> plugin.getMessages().getString(
                    (offlinePlayer.isOnline()
                            && plugin.getSpyManager().isSpy(offlinePlayer.getPlayer()))
                            ? "placeholders.spy.enabled"
                            : "placeholders.spy.disabled");

            case "ignored" -> plugin.getMessages().getString(
                    (offlinePlayer.isOnline()
                            && plugin.getIgnoreManager().hasIgnoredPlayers(offlinePlayer.getUniqueId()))
                            ? "placeholders.ignored.true"
                            : "placeholders.ignored.false");

            case "ignored_count" -> {
                if (!offlinePlayer.isOnline()) yield "0";
                yield String.valueOf(plugin.getIgnoreManager()
                        .getIgnoredPlayers(offlinePlayer.getUniqueId()).size());
            }
            case "last_sender" -> {
                if (!offlinePlayer.isOnline()) yield "None";
                UUID last = plugin.getMessageHistory().getLastSender(offlinePlayer.getUniqueId());
                yield last == null ? "None" : Bukkit.getOfflinePlayer(last).getName();
            }
            case "last_message_part" -> {
                if (!offlinePlayer.isOnline()) yield "—";
                String last = plugin.getMessageHistory().getLastMessage(offlinePlayer.getUniqueId());
                if (last == null) yield "—";
                yield last.length() > 30 ? last.substring(0, 30) + "…" : last;
            }
            case "last_seen" -> {
                if (!offlinePlayer.isOnline()) yield plugin.getMessages().getString("placeholders.last_seen.none");
                long minutes = plugin.getMessageHistory().getMinutesSinceLastReceived(offlinePlayer.getUniqueId());
                if (minutes < 0) yield plugin.getMessages().getString("placeholders.last_seen.none");
                java.util.Map<String, String> ph = new HashMap<>();
                ph.put("minutes", String.valueOf(minutes));
                yield plugin.getMessages().getString("placeholders.last_seen.recent", ph);
            }
            case "dnd_enabled" -> (offlinePlayer.isOnline()
                    && plugin.getDndManager() != null
                    && plugin.getDndManager().isDnd(offlinePlayer.getUniqueId()))
                    ? plugin.getMessages().getString("placeholders.dnd.enabled")
                    : plugin.getMessages().getString("placeholders.dnd.disabled");

            default -> null;
        };
    }
}