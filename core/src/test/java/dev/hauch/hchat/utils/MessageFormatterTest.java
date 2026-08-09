package dev.hauch.hchat.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageFormatterTest {

    private static final PlainTextComponentSerializer PLAIN =
            PlainTextComponentSerializer.plainText();

    @Test
    void stripsLegacyColorCodes() {
        assertEquals("Hello", PLAIN.serialize(MessageFormatter.format("&aHello")));
    }

    @Test
    void parsesHexColors() {
        assertEquals("Hi", PLAIN.serialize(MessageFormatter.format("&#54A3FFHi")));
    }

    @Test
    void replacesPlaceholders() {
        Component result = MessageFormatter.format(
                "&7[{sender}] {message}",
                Map.of("sender", "Notch", "message", "hola"));
        assertEquals("[Notch] hola", PLAIN.serialize(result));
    }

    @Test
    void emptyInputProducesEmptyComponent() {
        assertEquals("", PLAIN.serialize(MessageFormatter.format(null)));
        assertEquals("", PLAIN.serialize(MessageFormatter.format("")));
    }

    @Test
    void hoverLinesJoinWithNewlines() {
        Component hover = MessageFormatter.formatHover(
                List.of("&7Line one", "&7Line two"),
                Map.of());
        String[] lines = PLAIN.serialize(hover).split("\n");
        assertEquals(2, lines.length);
        assertEquals("Line one", lines[0]);
        assertEquals("Line two", lines[1]);
    }

    @Test
    void clickActionIsAttached() {
        Component component = MessageFormatter.withSuggestCommand(
                Component.text("reply"), "/msg Notch");
        assertTrue(component.clickEvent() != null);
        assertEquals("/msg Notch", component.clickEvent().value());
    }
}
