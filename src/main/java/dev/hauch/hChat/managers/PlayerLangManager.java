package dev.hauch.hChat.managers;

import dev.hauch.hChat.storage.PlayerLangStorage;

import java.util.*;

public class PlayerLangManager {

    private final Map<UUID, String> langs = new HashMap<>();
    private final PlayerLangStorage storage;

    public PlayerLangManager(PlayerLangStorage storage) {
        this.storage = storage;
        if (storage != null) langs.putAll(storage.loadAll());
    }

    public void set(UUID player, String lang) {
        if (lang == null) {
            langs.remove(player);
            if (storage != null) storage.remove(player);
        } else {
            langs.put(player, lang);
            if (storage != null) storage.save(player, lang);
        }
    }

    public Optional<String> get(UUID player) {
        return Optional.ofNullable(langs.get(player));
    }

    public Map<UUID, String> all() {
        return Collections.unmodifiableMap(langs);
    }

    public void saveAll() {
        if (storage == null) return;
        for (Map.Entry<UUID, String> e : langs.entrySet()) {
            storage.save(e.getKey(), e.getValue());
        }
    }
}