package dev.hauch.hChat.utils;

import dev.hauch.hChat.HChat;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BroadcastRenderer {

    private BroadcastRenderer() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void broadcast(HChat plugin, String message) {
        Component component = build(plugin, message);
        Bukkit.broadcast(component);
    }

    public static Component build(HChat plugin, String message) {
        List<String> formatLines = plugin.getConfigManager().getBroadcastFormat();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("message", message);

        if (formatLines == null || formatLines.isEmpty()) {
            return MessageFormatter.format("&eBroadcast: &b" + message);
        }

        Component result = Component.empty();
        for (int i = 0; i < formatLines.size(); i++) {
            if (i > 0) result = result.append(Component.newline());
            String line = formatLines.get(i);
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                line = line.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            result = result.append(MessageFormatter.format(line));
        }
        return result;
    }
}