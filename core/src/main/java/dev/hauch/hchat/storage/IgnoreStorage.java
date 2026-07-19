package dev.hauch.hchat.storage;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

// interface IgnoreStorage
public interface IgnoreStorage {
    Map<UUID, Set<UUID>> loadAll();
    void save(UUID player, Set<UUID> ignored);
}