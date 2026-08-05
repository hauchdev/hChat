package dev.hauch.hchat.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Persists the channel each player is currently tuned to in
 * {@code storage/player-channels.yml}, mirroring PlayerLangStorage.
 */
public class PlayerChannelStorage {

    private final Plugin plugin;
    private final File file;
    private YamlConfiguration yaml;
    private boolean loadFailed;

    // make PlayerChannelStorage
    public PlayerChannelStorage(Plugin plugin) {
        this.plugin = plugin;
        File folder = new File(plugin.getDataFolder(), "storage");
        if (!folder.exists()) folder.mkdirs();
        this.file = new File(folder, "player-channels.yml");
        this.yaml = new YamlConfiguration();
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException ignored) { }
        }
        try { yaml.load(file); }
        catch (Exception e) {
            plugin.getLogger().warning("Could not parse player-channels.yml - starting empty.");
            loadFailed = true;
            yaml = new YamlConfiguration();
        }
    }

    // load all
    public Map<UUID, String> loadAll() {
        Map<UUID, String> result = new HashMap<>();
        if (loadFailed) return result;
        if (yaml.getConfigurationSection("players") == null) return result;
        for (String key : yaml.getConfigurationSection("players").getKeys(false)) {
            try {
                result.put(UUID.fromString(key),
                        yaml.getString("players." + key));
            } catch (IllegalArgumentException ignored) {}
        }
        return result;
    }

    // save data
    public void save(UUID player, String channel) {
        if (loadFailed) return;
        yaml.set("players." + player.toString(), channel);
        try { yaml.save(file); }
        catch (IOException e) {
            plugin.getLogger().warning("Failed to save player-channels.yml: " + e.getMessage());
        }
    }

    // remove data
    public void remove(UUID player) {
        if (loadFailed) return;
        yaml.set("players." + player.toString(), null);
        try { yaml.save(file); }
        catch (IOException e) {
            plugin.getLogger().warning("Failed to update player-channels.yml: " + e.getMessage());
        }
    }
}
