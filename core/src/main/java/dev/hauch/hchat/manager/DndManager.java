package dev.hauch.hchat.manager;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// class DndManager
public class DndManager {

    private final Set<UUID> dndPlayers = new HashSet<>();

    // enable dnd
    public boolean enableDnd(UUID playerUuid) {
        return dndPlayers.add(playerUuid);
    }

    // disable dnd
    public boolean disableDnd(UUID playerUuid) {
        return dndPlayers.remove(playerUuid);
    }

    // toggle dnd
    public boolean toggleDnd(UUID playerUuid) {
        if (dndPlayers.contains(playerUuid)) {
            dndPlayers.remove(playerUuid);
            return false;
        } else {
            dndPlayers.add(playerUuid);
            return true;
        }
    }

    // is dnd
    public boolean isDnd(UUID playerUuid) {
        return dndPlayers.contains(playerUuid);
    }

    // is dnd
    public boolean isDnd(Player player) {
        return dndPlayers.contains(player.getUniqueId());
    }

    // get dnd players
    public Set<UUID> getDndPlayers() {
        return new HashSet<>(dndPlayers);
    }
}
