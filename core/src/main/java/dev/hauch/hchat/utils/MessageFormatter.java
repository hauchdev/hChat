package dev.hauch.hchat.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// class MessageFormatter
public final class MessageFormatter {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER =
            LegacyComponentSerializer.builder()
                    .character('&')
                    .hexColors()
                    .build();

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /** &#RRGGBB -> <#RRGGBB> so legacy hex keeps working in MiniMessage mode. */
    private static final Pattern LEGACY_HEX = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private static final Pattern LEGACY_CODE = Pattern.compile("&[0-9a-fk-orA-FK-OR]");

    /** MiniMessage opening/closing tag heuristic used by AUTO mode. */
    private static final Pattern MINI_TAG =
            Pattern.compile("</?[a-zA-Z#][a-zA-Z0-9_-]*(\\s[^<>]*)?>");

    /** Classic & code -> MiniMessage tag (lowercase code char). */
    private static final Map<Character, String> LEGACY_TO_MINI = Map.ofEntries(
            Map.entry('0', "<black>"),
            Map.entry('1', "<dark_blue>"),
            Map.entry('2', "<dark_green>"),
            Map.entry('3', "<dark_aqua>"),
            Map.entry('4', "<dark_red>"),
            Map.entry('5', "<dark_purple>"),
            Map.entry('6', "<gold>"),
            Map.entry('7', "<gray>"),
            Map.entry('8', "<dark_gray>"),
            Map.entry('9', "<blue>"),
            Map.entry('a', "<green>"),
            Map.entry('b', "<aqua>"),
            Map.entry('c', "<red>"),
            Map.entry('d', "<light_purple>"),
            Map.entry('e', "<yellow>"),
            Map.entry('f', "<white>"),
            Map.entry('k', "<obfuscated>"),
            Map.entry('l', "<bold>"),
            Map.entry('m', "<strikethrough>"),
            Map.entry('n', "<underlined>"),
            Map.entry('o', "<italic>"),
            Map.entry('r', "<reset>"));

    /** Active parse mode; defaults to LEGACY for backwards compatibility. */
    private static volatile FormatMode mode = FormatMode.LEGACY;

    // make MessageFormatter
    private MessageFormatter() {
        throw new UnsupportedOperationException("Utility class");
    }

    /** Sets the global parse mode (called from Bootstrap on start/reload). */
    public static void setMode(FormatMode newMode) {
        mode = newMode == null ? FormatMode.LEGACY : newMode;
    }

    /** Active global parse mode. */
    public static FormatMode mode() {
        return mode;
    }

    // format data
    public static Component format(String text) {
        return format(text, mode);
    }

    // format data with an explicit mode (used for per-channel overrides)
    public static Component format(String text, FormatMode formatMode) {
        if (text == null || text.isEmpty()) return Component.empty();
        FormatMode effective = formatMode == null ? mode : formatMode;
        return switch (effective) {
            case MINIMESSAGE -> MINI_MESSAGE.deserialize(translateLegacy(text));
            case AUTO -> looksLegacy(text) || !MINI_TAG.matcher(text).find()
                    ? LEGACY_SERIALIZER.deserialize(text)
                    : MINI_MESSAGE.deserialize(translateLegacy(text));
            case LEGACY -> LEGACY_SERIALIZER.deserialize(text);
        };
    }

    // format data
    public static Component format(String template, Map<String, String> placeholders) {
        if (template == null || template.isEmpty()) return Component.empty();

        if (mode == FormatMode.LEGACY) {
            String result = template;
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                result = result.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            return LEGACY_SERIALIZER.deserialize(result);
        }

        // MiniMessage/AUTO: placeholder values can carry player-typed text
        // (e.g. {message} in DMs), so they are escaped before insertion and
        // can never inject markup into the rendered component.
        String result = template;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String value = entry.getValue() == null ? "" : entry.getValue();
            result = result.replace("{" + entry.getKey() + "}", escapeMiniTags(value));
        }
        return MINI_MESSAGE.deserialize(translateLegacy(result));
    }

    /**
     * Formats text typed by a player. Under LEGACY this is identical to
     * format(); under MiniMessage/AUTO the player's markup is escaped first
     * so tags like click actions can never be injected, while classic
     * & codes keep working.
     */
    public static Component formatPlayerMessage(String text) {
        return formatPlayerMessage(text, mode);
    }

    // player message with explicit mode
    public static Component formatPlayerMessage(String text, FormatMode formatMode) {
        if (text == null || text.isEmpty()) return Component.empty();
        FormatMode effective = formatMode == null ? mode : formatMode;
        if (effective == FormatMode.LEGACY) {
            return LEGACY_SERIALIZER.deserialize(text);
        }
        return MINI_MESSAGE.deserialize(translateLegacy(escapeMiniTags(text)));
    }

    // format hover
    public static Component formatHover(List<String> lines, Map<String, String> placeholders) {
        if (lines == null || lines.isEmpty()) return Component.empty();
        Component hover = Component.empty();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) hover = hover.append(Component.newline());
            String line = lines.get(i);
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                line = line.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            hover = hover.append(format(line));
        }
        return hover;
    }

    // with hover
    public static Component withHover(Component component, Component hoverText) {
        return component.hoverEvent(HoverEvent.showText(hoverText));
    }

    // with suggest command
    public static Component withSuggestCommand(Component component, String command) {
        return component.clickEvent(ClickEvent.suggestCommand(command));
    }

    // with run command
    public static Component withRunCommand(Component component, String command) {
        return component.clickEvent(ClickEvent.runCommand(command));
    }

    // with open url
    public static Component withOpenUrl(Component component, String url) {
        return component.clickEvent(ClickEvent.openUrl(url));
    }

    // join data
    public static Component join(Component separator, Component... components) {
        Component result = Component.empty();
        for (int i = 0; i < components.length; i++) {
            if (i > 0) result = result.append(separator);
            result = result.append(components[i]);
        }
        return result;
    }

    // to plain text
    public static String toPlainText(Component component) {
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
                .plainText().serialize(component);
    }

    /**
     * Escapes MiniMessage markup in untrusted text: backslashes and angle
     * brackets are neutralized so MiniMessage renders them literally.
     */
    private static String escapeMiniTags(String text) {
        if (text.indexOf('<') < 0 && text.indexOf('\\') < 0) return text;
        StringBuilder sb = new StringBuilder(text.length() + 8);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\') {
                sb.append("\\\\");
                continue;
            }
            if (c == '<') sb.append('\\');
            sb.append(c);
        }
        return sb.toString();
    }

    /** Translates classic & codes and &#RRGGBB hex to MiniMessage tags. */
    private static String translateLegacy(String text) {
        if (text.indexOf('&') < 0) return text;
        Matcher hex = LEGACY_HEX.matcher(text);
        String withHex = hex.find() ? hex.replaceAll("<#$1>") : text;
        StringBuilder sb = new StringBuilder(withHex.length() + 16);
        for (int i = 0; i < withHex.length(); i++) {
            char c = withHex.charAt(i);
            if (c == '&' && i + 1 < withHex.length()) {
                String tag = LEGACY_TO_MINI.get(
                        Character.toLowerCase(withHex.charAt(i + 1)));
                if (tag != null) {
                    sb.append(tag);
                    i++;
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    // true when the string uses classic & codes (AUTO picks legacy first)
    private static boolean looksLegacy(String text) {
        return LEGACY_CODE.matcher(text).find();
    }
}