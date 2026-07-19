package dev.hauch.hchat.registry;

import org.bukkit.event.Listener;

// class ListenerRegistry
public final class ListenerRegistry extends Registry<Listener> {

    // make ListenerRegistry
    public ListenerRegistry() {
        super("Listener");
    }
}
