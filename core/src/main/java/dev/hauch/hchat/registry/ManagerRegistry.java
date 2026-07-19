package dev.hauch.hchat.registry;

import dev.hauch.hchat.manager.DndManager;
import dev.hauch.hchat.manager.IgnoreManager;
import dev.hauch.hchat.manager.LanguageManager;
import dev.hauch.hchat.manager.MessageHistory;
import dev.hauch.hchat.manager.OfflineMessageStore;
import dev.hauch.hchat.manager.PlayerLangManager;
import dev.hauch.hchat.manager.SpyManager;

// class ManagerRegistry
public final class ManagerRegistry extends Registry<Object> {

    // make ManagerRegistry
    public ManagerRegistry() {
        super("Manager");
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
}
