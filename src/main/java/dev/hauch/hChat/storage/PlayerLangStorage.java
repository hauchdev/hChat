package dev.hauch.hChat.storage;

import dev.hauch.hChat.HChat;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerLangStorage {

    private final HChat plugin;
    private final File file;
    private YamlConfiguration yaml;
    private boolean loadFailed;

    public PlayerLangStorage(HChat plugin) {
        this.plugin = plugin;
        File folder = new File(plugin.getDataFolder(), "storage");
        if (!folder.exists()) folder.mkdirs();
        this.file = new File(folder, "player-lang.yml");
        this.yaml = new YamlConfiguration();
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException ignored) {}
        }
        try { yaml.load(file); }
        catch (Exception e) {
            plugin.getLogger().warning("Could not parse player-lang.yml — starting empty.");
            loadFailed = true;
            yaml = new YamlConfiguration();
        }
    }

    public Map<UUID, String> loadAll() {
        Map<UUID, String> result = new HashMap<>();
        if (loadFailed) return result;
        if (yaml.getConfigurationSection("players") == null) return result;
        for (String key : yaml.getConfigurationSection("players").getKeys(false)) {
            try { result.put(UUID.fromString(key),
                    yaml.getString("players." + key)); }
            catch (IllegalArgumentException ignored) {}
        }
        return result;
    }

    public void save(UUID player, String lang) {
        if (loadFailed) return;
        yaml.set("players." + player.toString(), lang);
        try { yaml.save(file); }
        catch (IOException e) {
            plugin.getLogger().warning("Failed to save player-lang.yml: " + e.getMessage());
        }
    }

    public void remove(UUID player) {
        if (loadFailed) return;
        yaml.set("players." + player.toString(), null);
        try { yaml.save(file); }
        catch (IOException e) {
            plugin.getLogger().warning("Failed to update player-lang.yml: " + e.getMessage());
        }
    }
}