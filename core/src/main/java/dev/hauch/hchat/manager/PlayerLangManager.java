package dev.hauch.hchat.manager;

import dev.hauch.hchat.storage.PlayerLangStorage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// class PlayerLangManager
public class PlayerLangManager {

    private final Map<UUID, String> langs = new HashMap<>();
    private final PlayerLangStorage storage;

    // make PlayerLangManager
    public PlayerLangManager(PlayerLangStorage storage) {
        this.storage = storage;
        if (storage != null) langs.putAll(storage.loadAll());
    }

    // set data
    public void set(UUID player, String lang) {
        if (lang == null) {
            langs.remove(player);
            if (storage != null) storage.remove(player);
        } else {
            langs.put(player, lang);
            if (storage != null) storage.save(player, lang);
        }
    }

    // get data
    public Optional<String> get(UUID player) {
        return Optional.ofNullable(langs.get(player));
    }

    // all data
    public Map<UUID, String> all() {
        return Collections.unmodifiableMap(langs);
    }

    // save all
    public void saveAll() {
        if (storage == null) return;
        for (Map.Entry<UUID, String> e : langs.entrySet()) {
            storage.save(e.getKey(), e.getValue());
        }
    }
}