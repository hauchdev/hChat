package dev.hauch.hchat.manager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * In-memory ring buffer of the most recent chat lines (capacity from
 * {@code replay.capacity}, default 50). Used by {@code /hchat replay} to
 * reprint recent chat, optionally filtered by player name.
 */
public final class ChatReplay {

    private final int capacity;
    private final Deque<String> lines = new ArrayDeque<>();

    // make ChatReplay
    public ChatReplay(int capacity) {
        this.capacity = Math.max(1, capacity);
    }

    // record a line
    public synchronized void record(String line) {
        while (lines.size() >= capacity) {
            lines.pollFirst();
        }
        lines.addLast(line);
    }

    // last n lines in chronological order
    public synchronized List<String> last(int n) {
        if (lines.isEmpty()) return Collections.emptyList();
        int count = Math.min(n, lines.size());
        return new ArrayList<>(lines).subList(lines.size() - count, lines.size());
    }

    // last n lines whose stored prefix contains ": name:"
    public synchronized List<String> lastFor(String playerName, int n) {
        if (playerName == null || lines.isEmpty()) return Collections.emptyList();
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            if (line.contains(" " + playerName + ":")) {
                result.add(line);
            }
        }
        int count = Math.min(n, result.size());
        return new ArrayList<>(result.subList(result.size() - count, result.size()));
    }

    // clear the buffer
    public synchronized void clear() {
        lines.clear();
    }
}
