package dev.hauch.hChat.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageHistory {

    private final Map<UUID, UUID> lastSenderMap = new HashMap<>();

    public void setLastSender(UUID receiver, UUID sender) {
        lastSenderMap.put(receiver, sender);
    }

    public UUID getLastSender(UUID playerUuid) {
        return lastSenderMap.get(playerUuid);
    }

    public boolean hasLastSender(UUID playerUuid) {
        return lastSenderMap.containsKey(playerUuid);
    }

    public void clear(UUID playerUuid) {
        lastSenderMap.remove(playerUuid);
    }
}