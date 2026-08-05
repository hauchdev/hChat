package dev.hauch.hchat.registry;

import dev.hauch.hchat.service.BroadcastService;
import dev.hauch.hchat.service.ConfigurationService;
import dev.hauch.hchat.service.DynamicPlaceholderResolver;
import dev.hauch.hchat.utils.ChatLogger;
import dev.hauch.hchat.manager.AutoBroadcastManager;

import java.util.Objects;

// class ServiceRegistry
public final class ServiceRegistry extends Registry<Object> {

    public static final String CONFIGURATION = "configuration";
    public static final String BROADCAST = "broadcast";
    public static final String AUTO_BROADCAST = "auto-broadcast";
    public static final String CHAT_LOGGER = "chat-logger";
    public static final String PLACEHOLDER_RESOLVER = "placeholder-resolver";

    // make ServiceRegistry
    public ServiceRegistry() {
        super("Service");
    }

    // add configuration service
    public void registerConfigurationService(ConfigurationService service) {
        register(CONFIGURATION, service);
    }

    // configuration service
    public ConfigurationService configurationService() {
        return typed(CONFIGURATION, ConfigurationService.class);
    }

    // add broadcast service
    public void registerBroadcastService(BroadcastService service) {
        register(BROADCAST, service);
    }

    // broadcast service
    public BroadcastService broadcastService() {
        return typed(BROADCAST, BroadcastService.class);
    }

    // add auto broadcast manager
    public void registerAutoBroadcastManager(AutoBroadcastManager manager) {
        register(AUTO_BROADCAST, manager);
    }

    // auto broadcast manager
    public AutoBroadcastManager autoBroadcastManager() {
        return typed(AUTO_BROADCAST, AutoBroadcastManager.class);
    }

    // add chat logger
    public void registerChatLogger(ChatLogger logger) {
        register(CHAT_LOGGER, logger);
    }

    // chat logger
    public ChatLogger chatLogger() {
        return typed(CHAT_LOGGER, ChatLogger.class);
    }

    // add placeholder resolver
    public void registerPlaceholderResolver(DynamicPlaceholderResolver resolver) {
        register(PLACEHOLDER_RESOLVER, resolver);
    }

    // placeholder resolver
    public DynamicPlaceholderResolver placeholderResolver() {
        return typed(PLACEHOLDER_RESOLVER, DynamicPlaceholderResolver.class);
    }

    // typed data
    private <T> T typed(String key, Class<T> type) {
        Object value = get(key);
        Objects.requireNonNull(value, () -> "Service '" + key + "' was never registered");
        if (!type.isInstance(value)) {
            throw new IllegalStateException(
                    "Service '" + key + "' is " + value.getClass().getName()
                            + " - expected " + type.getName());
        }
        return type.cast(value);
    }
}
