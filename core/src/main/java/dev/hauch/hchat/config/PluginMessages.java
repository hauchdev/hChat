package dev.hauch.hchat.config;

import dev.hauch.hchat.manager.LanguageManager;
import dev.hauch.hchat.utils.MessageFormatter;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;

import java.util.Map;

// class PluginMessages
public class PluginMessages {

    private final Plugin plugin;
    private final LanguageManager languageManager;

    // make PluginMessages
    public PluginMessages(Plugin plugin) {
        this.plugin = plugin;
        this.languageManager = new LanguageManager(plugin);
        loadLanguage();
    }

    // load language
    public void loadLanguage() {
        String lang = plugin.getConfig().getString("lang", "en");
        languageManager.setLanguage(lang);
    }

    // reload data
    public void reload() {
        languageManager.reload();
        loadLanguage();
    }

    // get string
    public String getString(String path) {
        return languageManager.getMessage(path);
    }

    // get component
    public Component getComponent(String path) {
        return MessageFormatter.format(getString(path));
    }

    // get component
    public Component getComponent(String path, Map<String, String> placeholders) {
        return MessageFormatter.format(getString(path), placeholders);
    }

    // get string
    public String getString(String path, Map<String, String> placeholders) {
        String raw = getString(path);
        if (raw == null || raw.isEmpty() || placeholders == null || placeholders.isEmpty()) {
            return raw;
        }
        String result = raw;
        for (Map.Entry<String, String> e : placeholders.entrySet()) {
            result = result.replace("{" + e.getKey() + "}", e.getValue());
        }
        return result;
    }
}