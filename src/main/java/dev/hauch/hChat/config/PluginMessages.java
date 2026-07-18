package dev.hauch.hChat.config;

import dev.hauch.hChat.HChat;
import dev.hauch.hChat.managers.LanguageManager;
import dev.hauch.hChat.utils.MessageFormatter;
import net.kyori.adventure.text.Component;

public class PluginMessages {

    private final LanguageManager languageManager;
    private final HChat plugin;

    public PluginMessages(HChat plugin) {
        this.plugin = plugin;
        this.languageManager = new LanguageManager(plugin.getDataFolder(), plugin);
        loadLanguage();
    }

    public void loadLanguage() {
        String lang = plugin.getConfig().getString("lang", "en");
        languageManager.setLanguage(lang);
    }

    public void reload() {
        languageManager.reload();
        loadLanguage();
    }

    public String getString(String path) {
        return languageManager.getMessage(path);
    }

    public Component getComponent(String path) {
        return MessageFormatter.format(getString(path));
    }

    public Component getComponent(String path, java.util.Map<String, String> placeholders) {
        return MessageFormatter.format(getString(path), placeholders);
    }

    public String getString(String path, java.util.Map<String, String> placeholders) {
        String raw = getString(path);
        if (raw == null || raw.isEmpty() || placeholders == null || placeholders.isEmpty()) {
            return raw;
        }
        String result = raw;
        for (java.util.Map.Entry<String, String> e : placeholders.entrySet()) {
            result = result.replace("{" + e.getKey() + "}", e.getValue());
        }
        return result;
    }

}