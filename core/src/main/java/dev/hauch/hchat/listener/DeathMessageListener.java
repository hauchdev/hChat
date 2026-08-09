package dev.hauch.hchat.listener;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.utils.MessageFormatter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Replaces the vanilla death message with a configurable one
 * ({@code death-message.*} in config.yml). Supports per-cause overrides
 * under {@code death-message.custom.<CAUSE>} and the placeholders
 * {@code {player}} and {@code {cause}}.
 */
public class DeathMessageListener implements Listener {

    private final PluginConfig config;

    // make DeathMessageListener
    public DeathMessageListener(PluginConfig config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    // on death
    public void onDeath(PlayerDeathEvent event) {
        if (!config.isDeathMessageEnabled()) return;

        String cause = event.getEntity().getLastDamageCause() != null
                ? event.getEntity().getLastDamageCause().getCause().name()
                : "UNKNOWN";

        String template = config.getDeathMessageCustom(cause);
        if (template == null) {
            template = config.getDeathMessageFormat();
        }

        if (template == null || template.isBlank()) {
            // empty format = hide death messages entirely
            event.deathMessage(null);
            return;
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", event.getEntity().getName());
        placeholders.put("cause", cause.toLowerCase(Locale.ROOT).replace('_', ' '));
        event.deathMessage(MessageFormatter.format(template, placeholders));
    }
}
