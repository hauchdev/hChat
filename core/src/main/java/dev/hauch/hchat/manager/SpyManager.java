package dev.hauch.hchat.manager;

import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// class SpyManager
public class SpyManager {

    private final Set<UUID> spies = new HashSet<>();

    // enable spy
    public boolean enableSpy(UUID playerUuid) {
        return spies.add(playerUuid);
    }

    // disable spy
    public boolean disableSpy(UUID playerUuid) {
        return spies.remove(playerUuid);
    }

    // toggle spy
    public boolean toggleSpy(UUID playerUuid) {
        if (spies.contains(playerUuid)) {
            spies.remove(playerUuid);
            return false;
        } else {
            spies.add(playerUuid);
            return true;
        }
    }

    // is spy
    public boolean isSpy(UUID playerUuid) {
        return spies.contains(playerUuid);
    }

    // is spy
    public boolean isSpy(Player player) {
        return spies.contains(player.getUniqueId());
    }

    // get spies
    public Set<UUID> getSpies() {
        return new HashSet<>(spies);
    }
}