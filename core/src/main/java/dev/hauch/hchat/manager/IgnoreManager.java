package dev.hauch.hchat.manager;

import dev.hauch.hchat.storage.IgnoreStorage;
import org.bukkit.entity.Player;

import java.util.*;

// class IgnoreManager
public class IgnoreManager {

    private final Map<UUID, Set<UUID>> ignoredMap = new HashMap<>();
    private final IgnoreStorage storage;

    // make IgnoreManager
    public IgnoreManager() {
        this(null);
    }

    // make IgnoreManager
    public IgnoreManager(IgnoreStorage storage) {
        this.storage = storage;
        if (storage != null) {
            this.ignoredMap.putAll(storage.loadAll());
        }
    }

    // ignore player
    public boolean ignorePlayer(UUID ignorer, UUID target) {
        boolean added = ignoredMap.computeIfAbsent(ignorer, k -> new HashSet<>()).add(target);
        if (added && storage != null) storage.save(ignorer, ignoredMap.get(ignorer));
        return added;
    }

    // unignore player
    public boolean unignorePlayer(UUID ignorer, UUID target) {
        Set<UUID> ignored = ignoredMap.get(ignorer);
        if (ignored == null) return false;
        boolean removed = ignored.remove(target);
        if (removed && storage != null) storage.save(ignorer, ignored);
        return removed;
    }

    // is ignored
    public boolean isIgnored(UUID ignorer, UUID target) {
        Set<UUID> ignored = ignoredMap.get(ignorer);
        return ignored != null && ignored.contains(target);
    }

    // is ignored
    public boolean isIgnored(Player ignorer, Player target) {
        return isIgnored(ignorer.getUniqueId(), target.getUniqueId());
    }

    // get ignored players
    public Set<UUID> getIgnoredPlayers(UUID playerUuid) {
        return ignoredMap.getOrDefault(playerUuid, Collections.emptySet());
    }

    // has ignored players
    public boolean hasIgnoredPlayers(UUID playerUuid) {
        Set<UUID> ignored = ignoredMap.get(playerUuid);
        return ignored != null && !ignored.isEmpty();
    }

    // reload data
    public void reload() {
        if (storage == null) return;
        ignoredMap.clear();
        ignoredMap.putAll(storage.loadAll());
    }
}