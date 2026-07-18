package dev.hauch.hChat.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;
import java.util.Map;

public final class MessageFormatter {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER =
            LegacyComponentSerializer.builder()
                    .character('&')
                    .hexColors()
                    .build();

    private MessageFormatter() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Component format(String text) {
        if (text == null || text.isEmpty()) return Component.empty();
        return LEGACY_SERIALIZER.deserialize(text);
    }

    public static Component format(String template, Map<String, String> placeholders) {
        if (template == null || template.isEmpty()) return Component.empty();
        String result = template;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return LEGACY_SERIALIZER.deserialize(result);
    }

    public static Component formatHover(List<String> lines, Map<String, String> placeholders) {
        if (lines == null || lines.isEmpty()) return Component.empty();
        Component hover = Component.empty();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) hover = hover.append(Component.newline());
            String line = lines.get(i);
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                line = line.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            hover = hover.append(LEGACY_SERIALIZER.deserialize(line));
        }
        return hover;
    }

    public static Component withHover(Component component, Component hoverText) {
        return component.hoverEvent(HoverEvent.showText(hoverText));
    }

    public static Component withSuggestCommand(Component component, String command) {
        return component.clickEvent(ClickEvent.suggestCommand(command));
    }

    public static Component withRunCommand(Component component, String command) {
        return component.clickEvent(ClickEvent.runCommand(command));
    }

    public static Component withOpenUrl(Component component, String url) {
        return component.clickEvent(ClickEvent.openUrl(url));
    }

    public static Component join(Component separator, Component... components) {
        Component result = Component.empty();
        for (int i = 0; i < components.length; i++) {
            if (i > 0) result = result.append(separator);
            result = result.append(components[i]);
        }
        return result;
    }

    public static String toPlainText(Component component) {
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
                .plainText().serialize(component);
    }
}