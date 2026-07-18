package dev.hauch.hChat.placeholders;

import dev.hauch.hChat.HChat;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

        return switch (params.toLowerCase()) {
            case "spy_enabled" -> {
                if (offlinePlayer.isOnline()) {
                    Player player = offlinePlayer.getPlayer();
                    if (plugin.getSpyManager().isSpy(player)) {
                        yield plugin.getMessages().getString("placeholders.spy.enabled");
                    }
                }
                yield plugin.getMessages().getString("placeholders.spy.disabled");
            }
            case "ignored" -> {
                if (offlinePlayer.isOnline()) {
                    Player player = offlinePlayer.getPlayer();
                    if (plugin.getIgnoreManager().hasIgnoredPlayers(player.getUniqueId())) {
                        yield plugin.getMessages().getString("placeholders.ignored.true");
                    }
                }
                yield plugin.getMessages().getString("placeholders.ignored.false");
            }
            default -> null;
        };
    }
}