package dev.hauch.hchat.utils.filter;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.utils.FilterAction;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Optional;

/**
 * Blocks (or masks / lowercases) messages that are mostly uppercase, e.g.
 * "HELLO EVERYONE". Only messages at least {@code min-length} characters
 * long are considered.
 */
public final class AntiCapsFilter implements ChatFilter {

    private final PluginConfig config;

    // make AntiCapsFilter
    public AntiCapsFilter(PluginConfig config) {
        this.config = config;
    }

    @Override
    public boolean isEnabled() {
        return config.isAntiCapsEnabled();
    }

    @Override
    public FilterAction action() {
        return config.getAntiCapsAction();
    }

    @Override
    public Optional<String> check(Player player, String message) {
        if (!isEnabled() || message == null || message.isBlank()) {
            return Optional.empty();
        }
        if (message.length() < config.getAntiCapsMinLength()) {
            return Optional.empty();
        }

        int upper = 0;
        for (char c : message.toCharArray()) {
            if (Character.isUpperCase(c)) upper++;
        }
        if (upper == 0) return Optional.empty();

        int percent = upper * 100 / message.length();
        if (percent <= config.getAntiCapsMaxPercent()) {
            return Optional.empty();
        }

        return switch (action()) {
            case LOWERCASE -> Optional.of(message.toLowerCase(Locale.ROOT));
            case MASK -> Optional.of("*".repeat(message.length()));
            case WARN, BLOCK -> Optional.of("__BLOCKED__");
        };
    }
}
