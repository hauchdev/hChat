package dev.hauch.hchat.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// class YamlIgnoreStorage
public class YamlIgnoreStorage implements IgnoreStorage {

    private final Plugin plugin;
    private final File file;
    private YamlConfiguration yaml;
    private boolean loadFailed;

    // make YamlIgnoreStorage
    public YamlIgnoreStorage(Plugin plugin) {
        this.plugin = plugin;
        File folder = new File(plugin.getDataFolder(), "storage");
        if (!folder.exists()) folder.mkdirs();
        this.file = new File(folder, "ignored.yml");
        this.yaml = new YamlConfiguration();
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException ignored) { }
        }
        try {
            this.yaml.load(file);
        } catch (Exception e) {
            plugin.getLogger().warning("Could not parse ignored.yml - starting empty. " + e.getMessage());
            this.loadFailed = true;
            this.yaml = new YamlConfiguration();
        }
    }

    @Override
    // load all
    public Map<UUID, Set<UUID>> loadAll() {
        Map<UUID, Set<UUID>> result = new HashMap<>();
        if (loadFailed) return result;
        if (yaml.getConfigurationSection("players") == null) return result;
        for (String uuidStr : yaml.getConfigurationSection("players").getKeys(false)) {
            try {
                UUID id = UUID.fromString(uuidStr);
                List<String> raw = yaml.getStringList("players." + uuidStr);
                Set<UUID> set = new HashSet<>();
                for (String r : raw) {
                    try { set.add(UUID.fromString(r)); } catch (Exception ignored) {}
                }
                result.put(id, set);
            } catch (IllegalArgumentException ignored) {}
        }
        return result;
    }

    @Override
    // save data
    public void save(UUID player, Set<UUID> ignored) {
        if (loadFailed) {
            plugin.getLogger().warning("Skipping ignored.yml save this session (load failed earlier).");
            return;
        }
        List<String> raw = new ArrayList<>();
        for (UUID u : ignored) raw.add(u.toString());
        yaml.set("players." + player.toString(), raw);
        try { yaml.save(file); } catch (IOException e) {
            plugin.getLogger().warning("Failed to save ignored.yml: " + e.getMessage());
        }
    }
}