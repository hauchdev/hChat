package dev.hauch.hchat.manager;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.utils.MessageFormatter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Renders and routes staff chat messages ({@code /sc}). Recipients are the
 * online players holding {@code hchat.sc}; the message is also written to
 * the console when {@code staff-chat.log-to-console} is enabled.
 */
public final class StaffChatManager {

    private final PluginConfig config;

    // make StaffChatManager
    public StaffChatManager(PluginConfig config) {
        this.config = config;
    }

    // send a staff chat message to every online staff member
    public void send(Plugin plugin, Player sender, String message) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", sender.getName());
        placeholders.put("message", message);

        Component rendered = MessageFormatter.format(
                config.getStaffChatFormat(), placeholders);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("hchat.sc")) {
                p.sendMessage(rendered);
            }
        }

        if (config.isStaffChatLogToConsole()) {
            plugin.getLogger().info("[StaffChat] " + sender.getName() + ": " + message);
        }
    }
}
