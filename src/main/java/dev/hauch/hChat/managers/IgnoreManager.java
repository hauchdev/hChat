package dev.hauch.hChat.managers;

import org.bukkit.entity.Player;
import java.util.*;

public class IgnoreManager {

    private final Map<UUID, Set<UUID>> ignoredMap = new HashMap<>();

    public boolean ignorePlayer(UUID ignorer, UUID target) {
        return ignoredMap.computeIfAbsent(ignorer, k -> new HashSet<>()).add(target);
    }

    public boolean unignorePlayer(UUID ignorer, UUID target) {
        Set<UUID> ignored = ignoredMap.get(ignorer);
        return ignored != null && ignored.remove(target);
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
}