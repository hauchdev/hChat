package dev.hauch.hChat.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LanguageManager {

    private final File langsFolder;
    private FileConfiguration currentLang;
    private final Map<String, FileConfiguration> loadedLanguages;

    public LanguageManager(File dataFolder) {
        this.langsFolder = new File(dataFolder, "lang");
        this.loadedLanguages = new HashMap<>();
        setupLanguages();
    }

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

    private void createLanguageFile(String filename) {
        File langFile = new File(langsFolder, filename);
        if (!langFile.exists()) {
            try (InputStream in = getClass().getResourceAsStream("/lang/" + filename)) {
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

    public void setLanguage(String lang) {
        currentLang = loadedLanguages.getOrDefault(lang, loadedLanguages.get("en"));
        if (currentLang == null && !loadedLanguages.isEmpty()) {
            currentLang = loadedLanguages.values().iterator().next();
        }
    }

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

    public String getMessage(String key) {
        if (currentLang == null) return "Missing message: " + key;
        return currentLang.getString(key, "Missing message: " + key);
    }
}