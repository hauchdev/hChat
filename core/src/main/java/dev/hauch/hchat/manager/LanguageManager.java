package dev.hauch.hchat.manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

// class LanguageManager
public class LanguageManager {

    private final Plugin plugin;
    private final PlayerLangManager playerLangManager;
    private final File langsFolder;
    private final Map<String, FileConfiguration> loadedLanguages = new HashMap<>();
    private FileConfiguration currentLang;

    // make LanguageManager
    public LanguageManager(Plugin plugin) {
        this(plugin, null);
    }

    // make LanguageManager
    public LanguageManager(Plugin plugin, PlayerLangManager playerLangManager) {
        this.plugin = plugin;
        this.playerLangManager = playerLangManager;
        this.langsFolder = new File(plugin.getDataFolder(), "lang");
        setupLanguages();
    }

    // setup languages
    private void setupLanguages() {
        if (!langsFolder.exists()) {
            langsFolder.mkdirs();
        }

        createLanguageFile("lang_en.yml");

        File[] files = langsFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith("lang_") && file.getName().endsWith(".yml")) {
                    String langCode = file.getName()
                            .replace("lang_", "")
                            .replace(".yml", "");
                    loadedLanguages.put(langCode, YamlConfiguration.loadConfiguration(file));
                }
            }
        }
    }

    // make language file
    private void createLanguageFile(String filename) {
        File langFile = new File(langsFolder, filename);
        if (!langFile.exists()) {
            try (InputStream in = plugin.getResource("lang/" + filename)) {
                if (in != null) {
                    Files.copy(in, langFile.toPath());
                } else {
                    Bukkit.getLogger().warning("Language resource not found: /lang/" + filename);
                }
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING,
                        "Failed to create language file: {0}", e.getMessage());
            }
        }
    }

    // set language
    public void setLanguage(String lang) {
        currentLang = loadedLanguages.getOrDefault(lang, loadedLanguages.get("en"));
        if (currentLang == null && !loadedLanguages.isEmpty()) {
            currentLang = loadedLanguages.values().iterator().next();
        }
    }

    // reload data
    public void reload() {
        loadedLanguages.clear();
        setupLanguages();

        if (currentLang != null) {
            String currentCode = null;
            for (Map.Entry<String, FileConfiguration> entry : loadedLanguages.entrySet()) {
                if (entry.getValue() == currentLang) {
                    currentCode = entry.getKey();
                    break;
                }
            }
            if (currentCode != null) {
                setLanguage(currentCode);
            } else if (!loadedLanguages.isEmpty()) {
                setLanguage(loadedLanguages.containsKey("en") ? "en" :
                        loadedLanguages.keySet().iterator().next());
            }
        }
    }

    // get message
    public String getMessage(String key) {
        if (currentLang == null) return "Missing message: " + key;
        return currentLang.getString(key, "Missing message: " + key);
    }

    // get message
    public String getMessage(String key, UUID player) {
        String code = null;
        if (player != null && playerLangManager != null) {
            code = playerLangManager.get(player).orElse(null);
        }
        FileConfiguration target = code != null ? loadedLanguages.get(code) : null;
        if (target == null) target = currentLang;
        if (target == null) return "Missing message: " + key;
        return target.getString(key, "Missing message: " + key);
    }

    // loaded languages
    public Map<String, FileConfiguration> loadedLanguages() {
        return loadedLanguages;
    }

    // current language
    public FileConfiguration currentLanguage() {
        return currentLang;
    }
}