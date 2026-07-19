package dev.hauch.hchat.service;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import net.kyori.adventure.text.Component;

import java.util.Map;

// class ConfigurationService
public final class ConfigurationService {

    private final PluginConfig config;
    private final PluginMessages messages;

    // make ConfigurationService
    public ConfigurationService(PluginConfig config, PluginMessages messages) {
        this.config = config;
        this.messages = messages;
    }

    // config data
    public PluginConfig config() {
        return config;
    }

    // messages data
    public PluginMessages messages() {
        return messages;
    }

    // message string
    public String messageString(String path) {
        return messages.getString(path);
    }

    // message component
    public Component messageComponent(String path) {
        return messages.getComponent(path);
    }

    // message component
    public Component messageComponent(String path, Map<String, String> placeholders) {
        return messages.getComponent(path, placeholders);
    }

    // reload data
    public void reload() {
        config.reloadConfig();
        messages.reload();
    }
}
