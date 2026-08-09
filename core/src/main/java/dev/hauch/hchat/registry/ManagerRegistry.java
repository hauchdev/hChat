package dev.hauch.hchat.registry;

import dev.hauch.hchat.manager.AfkManager;
import dev.hauch.hchat.manager.ChannelManager;
import dev.hauch.hchat.manager.ChatLockManager;
import dev.hauch.hchat.manager.ChatReplay;
import dev.hauch.hchat.manager.DndManager;
import dev.hauch.hchat.manager.IgnoreManager;
import dev.hauch.hchat.manager.LanguageManager;
import dev.hauch.hchat.manager.MentionManager;
import dev.hauch.hchat.manager.MessageHistory;
import dev.hauch.hchat.manager.OfflineMessageStore;
import dev.hauch.hchat.manager.PlayerLangManager;
import dev.hauch.hchat.manager.SlowmodeManager;
import dev.hauch.hchat.manager.SpyManager;
import dev.hauch.hchat.manager.StaffChatManager;

// class ManagerRegistry
public final class ManagerRegistry extends Registry<Object> {

    // make ManagerRegistry
    public ManagerRegistry() {
        super("Manager");
    }

    // add channel manager
    public void registerChannelManager(ChannelManager manager) {
        register("channels", manager);
    }

    // add ignore manager
    public void registerIgnoreManager(IgnoreManager manager) {
        register("ignore", manager);
    }

    // add spy manager
    public void registerSpyManager(SpyManager manager) {
        register("spy", manager);
    }

    // add message history
    public void registerMessageHistory(MessageHistory manager) {
        register("message-history", manager);
    }

    // add offline message store
    public void registerOfflineMessageStore(OfflineMessageStore manager) {
        register("offline-messages", manager);
    }

    // add player lang manager
    public void registerPlayerLangManager(PlayerLangManager manager) {
        register("player-lang", manager);
    }

    // add language manager
    public void registerLanguageManager(LanguageManager manager) {
        register("language", manager);
    }

    // add dnd manager
    public void registerDndManager(DndManager manager) {
        register("dnd", manager);
    }

    // add slowmode manager
    public void registerSlowmodeManager(SlowmodeManager manager) {
        register("slowmode", manager);
    }

    // add chat lock manager
    public void registerChatLockManager(ChatLockManager manager) {
        register("chat-lock", manager);
    }

    // add mention manager
    public void registerMentionManager(MentionManager manager) {
        register("mentions", manager);
    }

    // add staff chat manager
    public void registerStaffChatManager(StaffChatManager manager) {
        register("staff-chat", manager);
    }

    // add chat replay
    public void registerChatReplay(ChatReplay replay) {
        register("chat-replay", replay);
    }

    // add afk manager
    public void registerAfkManager(AfkManager manager) {
        register("afk", manager);
    }
}
