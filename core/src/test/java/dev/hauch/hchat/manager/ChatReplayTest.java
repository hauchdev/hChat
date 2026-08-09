package dev.hauch.hchat.manager;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatReplayTest {

    @Test
    void emptyBufferReturnsEmpty() {
        ChatReplay replay = new ChatReplay(50);
        assertTrue(replay.last(50).isEmpty());
        assertTrue(replay.lastFor("Notch", 50).isEmpty());
    }

    @Test
    void returnsChronologicalOrder() {
        ChatReplay replay = new ChatReplay(50);
        replay.record("[global] A: one");
        replay.record("[global] B: two");
        replay.record("[global] C: three");

        assertEquals(List.of("[global] A: one", "[global] B: two",
                "[global] C: three"), replay.last(50));
    }

    @Test
    void capacityDropsOldest() {
        ChatReplay replay = new ChatReplay(2);
        replay.record("1");
        replay.record("2");
        replay.record("3");

        assertEquals(List.of("2", "3"), replay.last(50));
    }

    @Test
    void lastNCapsTheResult() {
        ChatReplay replay = new ChatReplay(50);
        for (int i = 1; i <= 5; i++) {
            replay.record("line " + i);
        }
        assertEquals(List.of("line 4", "line 5"), replay.last(2));
    }

    @Test
    void lastForFiltersByPlayer() {
        ChatReplay replay = new ChatReplay(50);
        replay.record("[global] Notch: hello");
        replay.record("[global] Steve: hi");
        replay.record("[global] Notch: again");

        List<String> notch = replay.lastFor("Notch", 50);
        assertEquals(2, notch.size());
        assertTrue(notch.get(0).contains("hello"));
        assertTrue(notch.get(1).contains("again"));
    }

    @Test
    void clearEmptiesTheBuffer() {
        ChatReplay replay = new ChatReplay(50);
        replay.record("x");
        replay.clear();
        assertTrue(replay.last(50).isEmpty());
    }
}
