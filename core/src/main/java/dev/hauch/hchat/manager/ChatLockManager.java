package dev.hauch.hchat.manager;

import dev.hauch.hchat.config.PluginConfig;
import org.bukkit.entity.Player;

/**
 * Global chat lock: while locked, no player without the bypass permission
 * can send chat messages. Staff can keep talking by holding the bypass
 * permission.
 */
public final class ChatLockManager {

    private final PluginConfig config;

    private volatile boolean locked;
    private volatile String lockedBy;
    private volatile long lockedAt;

    // make ChatLockManager
    public ChatLockManager(PluginConfig config) {
        this.config = config;
    }

    // is the chat locked (and the feature enabled)
    public boolean isLocked() {
        return config.isChatLockEnabled() && locked;
    }

    // can the player bypass the lock
    public boolean canBypass(Player player) {
        return player.hasPermission(config.getChatLockBypassPermission());
    }

    // lock or unlock the chat
    public void setLocked(boolean locked, String by) {
        this.locked = locked;
        this.lockedBy = locked ? by : null;
        this.lockedAt = System.currentTimeMillis();
    }

    // who locked the chat (null when unlocked)
    public String getLockedBy() {
        return lockedBy;
    }

    // when the chat was locked
    public long getLockedAt() {
        return lockedAt;
    }
}
