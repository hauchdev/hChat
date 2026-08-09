package dev.hauch.hchat.api.premium;

import dev.hauch.hchat.bootstrap.PluginContext;

import java.util.List;

/**
 * Extension point for the hChat Premium edition.
 * <p>
 * The free distribution never references this interface: {@code HChat}
 * calls {@code Bootstrap.initialize(plugin)} and no premium module is ever
 * created. The premium distribution ships its own {@code JavaPlugin} entry
 * point which calls {@code Bootstrap.initialize(plugin, premiumModule)} and
 * hands the assembled {@link PluginContext} to the module through
 * {@link #install(PluginContext)}.
 * <p>
 * The module may register commands, listeners and managers into the
 * existing registries, provide chat enhancers and hook into reload and
 * shutdown - everything the free edition can do, plus premium features.
 * Keep the implementation in a private repository: it must never be
 * committed to the public hChat repository.
 */
public interface PremiumModule {

    /**
     * Human-readable edition name used in log messages,
     * e.g. {@code "hChat Premium 1.2.6"}.
     */
    String edition();

    /**
     * Called right after the free core has been assembled and before
     * commands, listeners and tasks are bound, so everything registered
     * here participates in the normal bootstrap flow.
     *
     * @param context the fully assembled core context
     */
    void install(PluginContext context);

    /**
     * Called on {@code /hchat reload} after the free configuration reloaded.
     */
    default void reload() {
    }

    /**
     * Called on plugin shutdown after the free core started shutting down.
     */
    default void shutdown() {
    }

    /**
     * Enhancers applied by {@code ChatListener} while a message is
     * processed. Empty in the free edition.
     */
    default List<PremiumChatEnhancer> chatEnhancers() {
        return List.of();
    }
}
