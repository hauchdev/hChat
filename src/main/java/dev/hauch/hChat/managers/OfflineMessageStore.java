package dev.hauch.hChat.managers;

import java.util.*;

public class OfflineMessageStore {

    private final Map<UUID, List<String>> pending = new HashMap<>();
    private final int maxPerPlayer;

    public OfflineMessageStore(int maxPerPlayer) {
        this.maxPerPlayer = Math.max(1, maxPerPlayer);
    }

    public void save(UUID target, String renderedComponent) {
        List<String> list = pending.computeIfAbsent(target, k -> new ArrayList<>());
        while (list.size() >= maxPerPlayer) list.remove(0);
        list.add(renderedComponent);
    }

    public List<String> drain(UUID target) {
        List<String> list = pending.remove(target);
        return list == null ? Collections.emptyList() : list;
    }

    public boolean hasPending(UUID target) {
        List<String> list = pending.get(target);
        return list != null && !list.isEmpty();
    }
}