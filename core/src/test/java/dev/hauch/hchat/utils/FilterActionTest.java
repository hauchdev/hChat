package dev.hauch.hchat.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilterActionTest {

    @Test
    void parsesKnownValuesCaseInsensitively() {
        assertEquals(FilterAction.BLOCK, FilterAction.parse("block", FilterAction.WARN));
        assertEquals(FilterAction.BLOCK, FilterAction.parse("BLOCK", FilterAction.WARN));
        assertEquals(FilterAction.MASK, FilterAction.parse("mask", FilterAction.WARN));
        assertEquals(FilterAction.WARN, FilterAction.parse("warn", FilterAction.BLOCK));
        assertEquals(FilterAction.LOWERCASE, FilterAction.parse("lowercase", FilterAction.BLOCK));
    }

    @Test
    void trimsWhitespace() {
        assertEquals(FilterAction.MASK, FilterAction.parse("  mask  ", FilterAction.BLOCK));
    }

    @Test
    void fallsBackForUnknownValues() {
        assertEquals(FilterAction.WARN, FilterAction.parse("ban", FilterAction.WARN));
        assertEquals(FilterAction.WARN, FilterAction.parse("", FilterAction.WARN));
    }

    @Test
    void fallsBackForNull() {
        assertEquals(FilterAction.BLOCK, FilterAction.parse(null, FilterAction.BLOCK));
    }
}
