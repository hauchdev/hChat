package dev.hauch.hchat.service;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.utils.MessageFormatter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// class BroadcastService
public final class BroadcastService {

    private final PluginConfig config;

    // make BroadcastService
    public BroadcastService(PluginConfig config) {
        this.config = config;
    }

    // broadcast data
    public void broadcast(String message) {
        Component component = build(message);
        Bukkit.broadcast(component);
    }

    // build data
    public Component build(String message) {
        List<String> formatLines = config.getBroadcastFormat();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("message", message);

        if (formatLines == null || formatLines.isEmpty()) {
            return MessageFormatter.format("&eBroadcast: &b" + message);
        }

        Component result = Component.empty();
        for (int i = 0; i < formatLines.size(); i++) {
            if (i > 0) result = result.append(Component.newline());
            
            result = result.append(MessageFormatter.format(formatLines.get(i), placeholders));
        }
        return result;
    }
}
