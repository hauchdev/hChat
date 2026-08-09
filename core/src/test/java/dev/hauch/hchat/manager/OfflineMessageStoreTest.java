package dev.hauch.hchat.manager;

import dev.hauch.hchat.manager.OfflineMessageStore.OfflineMessage;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OfflineMessageStoreTest {

    private static final UUID TARGET = UUID.randomUUID();

    @Test
    void saveAndPeekKeepsOrder() {
        OfflineMessageStore store = new OfflineMessageStore(10);
        store.save(TARGET, new OfflineMessage("Notch", 1L, "a"));
        store.save(TARGET, new OfflineMessage("Steve", 2L, "b"));

        List<OfflineMessage> pending = store.peek(TARGET);
        assertEquals(2, pending.size());
        assertEquals("Notch", pending.get(0).senderName());
        assertEquals("Steve", pending.get(1).senderName());
        assertTrue(store.hasPending(TARGET));
    }

    @Test
    void drainRemovesEverything() {
        OfflineMessageStore store = new OfflineMessageStore(10);
        store.save(TARGET, new OfflineMessage("Notch", 1L, "a"));

        List<OfflineMessage> drained = store.drain(TARGET);
        assertEquals(1, drained.size());
        assertFalse(store.hasPending(TARGET));
        assertTrue(store.drain(TARGET).isEmpty());
    }

    @Test
    void clearRemovesEverything() {
        OfflineMessageStore store = new OfflineMessageStore(10);
        store.save(TARGET, new OfflineMessage("Notch", 1L, "a"));
        store.clear(TARGET);
        assertFalse(store.hasPending(TARGET));
    }

    @Test
    void maxPerPlayerEvictsOldestFirst() {
        OfflineMessageStore store = new OfflineMessageStore(2);
        store.save(TARGET, new OfflineMessage("A", 1L, "1"));
        store.save(TARGET, new OfflineMessage("B", 2L, "2"));
        store.save(TARGET, new OfflineMessage("C", 3L, "3"));

        List<OfflineMessage> pending = store.peek(TARGET);
        assertEquals(2, pending.size());
        assertEquals("B", pending.get(0).senderName());
        assertEquals("C", pending.get(1).senderName());
    }

    @Test
    void messagesArePerRecipient() {
        OfflineMessageStore store = new OfflineMessageStore(10);
        UUID other = UUID.randomUUID();
        store.save(TARGET, new OfflineMessage("A", 1L, "1"));

        assertTrue(store.hasPending(TARGET));
        assertFalse(store.hasPending(other));
        assertTrue(store.peek(other).isEmpty());
    }
}
