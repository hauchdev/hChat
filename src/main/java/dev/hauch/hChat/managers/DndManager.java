package dev.hauch.hChat.managers;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DndManager {

    private final Set<UUID> dndPlayers = new HashSet<>();

    public boolean enableDnd(UUID playerUuid) {
        return dndPlayers.add(playerUuid);
    }

    public boolean disableDnd(UUID playerUuid) {
        return dndPlayers.remove(playerUuid);
    }

    public boolean toggleDnd(UUID playerUuid) {
        if (dndPlayers.contains(playerUuid)) {
            dndPlayers.remove(playerUuid);
            return false;
        } else {
            dndPlayers.add(playerUuid);
            return true;
        }
    }

    public boolean isDnd(UUID playerUuid) {
        return dndPlayers.contains(playerUuid);
    }

    public boolean isDnd(Player player) {
        return dndPlayers.contains(player.getUniqueId());
    }

    public Set<UUID> getDndPlayers() {
        return new HashSet<>(dndPlayers);
    }
}
