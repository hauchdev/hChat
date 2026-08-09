package dev.hauch.hchat.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatChannelTest {

    private static ChatChannel channel(String format, int range, String alias,
                                       boolean perWorld, String seePermission) {
        return new ChatChannel("test", format, range, null, null,
                seePermission, 0L, alias, perWorld);
    }

    @Test
    void unlimitedWhenRangeNegative() {
        assertTrue(channel(null, -1, null, false, null).isUnlimited());
        assertFalse(channel(null, 80, null, false, null).isUnlimited());
    }

    @Test
    void blankFormatIsNoFormat() {
        assertFalse(channel(null, -1, null, false, null).hasFormat());
        assertFalse(channel("  ", -1, null, false, null).hasFormat());
        assertTrue(channel("&f{message}", -1, null, false, null).hasFormat());
    }

    @Test
    void aliasPresence() {
        assertFalse(channel(null, -1, null, false, null).hasAlias());
        assertFalse(channel(null, -1, "  ", false, null).hasAlias());
        assertTrue(channel(null, -1, "#staff", false, null).hasAlias());
    }

    @Test
    void perWorldFlag() {
        assertTrue(channel(null, -1, null, true, null).hasPerWorld());
        assertFalse(channel(null, -1, null, false, null).hasPerWorld());
    }

    @Test
    void seePermissionPresence() {
        assertFalse(channel(null, -1, null, false, null).hasSeePermission());
        assertTrue(channel(null, -1, null, false, "hchat.staff.see").hasSeePermission());
    }
}
