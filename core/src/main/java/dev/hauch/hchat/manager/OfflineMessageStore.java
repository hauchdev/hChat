package dev.hauch.hchat.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Queues private messages for offline recipients. Each entry keeps the
 * sender name and timestamp alongside the pre-rendered message component,
 * so {@code /hchat mail} can list who wrote and when. Messages are only
 * removed when the recipient reads or clears them ({@code /hchat mail}),
 * not automatically on join - joining shows a clickable summary instead.
 */
public class OfflineMessageStore {

    /** A single queued offline message. */
    public record OfflineMessage(String senderName, long timestamp, String rendered) {}

    private final Map<UUID, List<OfflineMessage>> pending = new HashMap<>();
    private final int maxPerPlayer;

    // make OfflineMessageStore
    public OfflineMessageStore(int maxPerPlayer) {
        this.maxPerPlayer = Math.max(1, maxPerPlayer);
    }

    // save data
    public void save(UUID target, OfflineMessage message) {
        List<OfflineMessage> list = pending.computeIfAbsent(target, k -> new ArrayList<>());
        while (list.size() >= maxPerPlayer) list.remove(0);
        list.add(message);
    }

    // peek without removing
    public List<OfflineMessage> peek(UUID target) {
        List<OfflineMessage> list = pending.get(target);
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    // drain data (read)
    public List<OfflineMessage> drain(UUID target) {
        List<OfflineMessage> list = pending.remove(target);
        return list == null ? Collections.emptyList() : list;
    }

    // clear data
    public void clear(UUID target) {
        pending.remove(target);
    }

    // has pending
    public boolean hasPending(UUID target) {
        List<OfflineMessage> list = pending.get(target);
        return list != null && !list.isEmpty();
    }
}