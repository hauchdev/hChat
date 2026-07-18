package dev.hauch.hChat.managers;

import dev.hauch.hChat.storage.IgnoreStorage;
import org.bukkit.entity.Player;

import java.util.*;

public class IgnoreManager {

    private final Map<UUID, Set<UUID>> ignoredMap = new HashMap<>();
    private final IgnoreStorage storage;

    public IgnoreManager() {
        this(null);
    }

    public IgnoreManager(IgnoreStorage storage) {
        this.storage = storage;
        if (storage != null) {
            this.ignoredMap.putAll(storage.loadAll());
        }
    }

    public boolean ignorePlayer(UUID ignorer, UUID target) {
        boolean added = ignoredMap.computeIfAbsent(ignorer, k -> new HashSet<>()).add(target);
        if (added && storage != null) storage.save(ignorer, ignoredMap.get(ignorer));
        return added;
    }

    public boolean unignorePlayer(UUID ignorer, UUID target) {
        Set<UUID> ignored = ignoredMap.get(ignorer);
        if (ignored == null) return false;
        boolean removed = ignored.remove(target);
        if (removed && storage != null) storage.save(ignorer, ignored);
        return removed;
    }

    public boolean isIgnored(UUID ignorer, UUID target) {
        Set<UUID> ignored = ignoredMap.get(ignorer);
        return ignored != null && ignored.contains(target);
    }

    public boolean isIgnored(Player ignorer, Player target) {
        return isIgnored(ignorer.getUniqueId(), target.getUniqueId());
    }

    public Set<UUID> getIgnoredPlayers(UUID playerUuid) {
        return ignoredMap.getOrDefault(playerUuid, Collections.emptySet());
    }

    public boolean hasIgnoredPlayers(UUID playerUuid) {
        Set<UUID> ignored = ignoredMap.get(playerUuid);
        return ignored != null && !ignored.isEmpty();
    }

    public void reload() {
        if (storage == null) return;
        ignoredMap.clear();
        ignoredMap.putAll(storage.loadAll());
    }
}