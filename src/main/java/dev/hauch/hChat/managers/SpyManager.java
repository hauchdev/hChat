package dev.hauch.hChat.managers;

import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpyManager {

    private final Set<UUID> spies = new HashSet<>();

    public boolean enableSpy(UUID playerUuid) {
        return spies.add(playerUuid);
    }

    public boolean disableSpy(UUID playerUuid) {
        return spies.remove(playerUuid);
    }

    public boolean toggleSpy(UUID playerUuid) {
        if (spies.contains(playerUuid)) {
            spies.remove(playerUuid);
            return false;
        } else {
            spies.add(playerUuid);
            return true;
        }
    }

    public boolean isSpy(UUID playerUuid) {
        return spies.contains(playerUuid);
    }

    public boolean isSpy(Player player) {
        return spies.contains(player.getUniqueId());
    }

    public Set<UUID> getSpies() {
        return new HashSet<>(spies);
    }
}