package dev.hauch.hchat.manager;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// class MessageHistory
public class MessageHistory {

    private final Map<UUID, UUID> lastSenderMap = new HashMap<>();
    private final Map<UUID, String> lastMessageMap = new HashMap<>();
    private final Map<UUID, Instant> lastReceivedAtMap = new HashMap<>();

    // record received
    public void recordReceived(UUID receiver, UUID sender, String message) {
        lastSenderMap.put(receiver, sender);
        lastMessageMap.put(receiver, message);
        lastReceivedAtMap.put(receiver, Instant.now());
    }

    // set last sender
    public void setLastSender(UUID receiver, UUID sender) {
        lastSenderMap.put(receiver, sender);
    }

    // get last sender
    public UUID getLastSender(UUID playerUuid) {
        return lastSenderMap.get(playerUuid);
    }

    // has last sender
    public boolean hasLastSender(UUID playerUuid) {
        return lastSenderMap.containsKey(playerUuid);
    }

    // get last message
    public String getLastMessage(UUID playerUuid) {
        return lastMessageMap.get(playerUuid);
    }

    // get minutes since last received
    public long getMinutesSinceLastReceived(UUID playerUuid) {
        Instant t = lastReceivedAtMap.get(playerUuid);
        if (t == null) return -1;
        return Duration.between(t, Instant.now()).toMinutes();
    }

    // clear data
    public void clear(UUID playerUuid) {
        lastSenderMap.remove(playerUuid);
        lastMessageMap.remove(playerUuid);
        lastReceivedAtMap.remove(playerUuid);
    }
}