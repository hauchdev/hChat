package dev.hauch.hChat.storage;


import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface IgnoreStorage {
    Map<UUID, Set<UUID>> loadAll();
    void save(UUID player, Set<UUID> ignored);
}