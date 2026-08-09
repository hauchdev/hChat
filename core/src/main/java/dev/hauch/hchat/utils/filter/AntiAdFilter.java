package dev.hauch.hchat.utils.filter;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.utils.FilterAction;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Blocks messages containing URLs / invites that match the configured
 * patterns (defaults: domain TLDs and Discord invites). Messages that
 * contain a whitelisted address pass untouched.
 */
public final class AntiAdFilter implements ChatFilter {

    private static final Pattern FAST_FAIL = Pattern.compile(
            "(discord|\\.|[a-z0-9-]+\\.(com|net|org|gg|me|io))",
            Pattern.CASE_INSENSITIVE);

    private final PluginConfig config;
    private final List<Pattern> patterns;
    private final List<String> whitelist;

    // make AntiAdFilter
    public AntiAdFilter(PluginConfig config) {
        this.config = config;
        this.patterns = new ArrayList<>();
        for (String raw : config.getAntiAdPatterns()) {
            if (raw == null || raw.isBlank()) continue;
            try {
                patterns.add(Pattern.compile(raw, Pattern.CASE_INSENSITIVE));
            } catch (RuntimeException ignored) {
                // a bad regex must not take the whole filter down
            }
        }
        this.whitelist = config.getAntiAdWhitelist() == null
                ? List.of() : List.copyOf(config.getAntiAdWhitelist());
    }

    @Override
    public boolean isEnabled() {
        return config.isAntiAdEnabled() && !patterns.isEmpty();
    }

    @Override
    public FilterAction action() {
        return config.getAntiAdAction();
    }

    @Override
    public Optional<String> check(Player player, String message) {
        if (!isEnabled() || message == null || message.isBlank()) {
            return Optional.empty();
        }
        if (!FAST_FAIL.matcher(message).find()) {
            return Optional.empty();
        }

        // whitelisted addresses pass
        String lower = message.toLowerCase(Locale.ROOT);
        for (String allowed : whitelist) {
            if (allowed != null && !allowed.isBlank() && lower.contains(allowed)) {
                return Optional.empty();
            }
        }

        for (Pattern pattern : patterns) {
            if (pattern.matcher(message).find()) {
                return switch (action()) {
                    case MASK -> Optional.of(pattern.matcher(message)
                            .replaceAll(m -> "*".repeat(m.group().length())));
                    case WARN, BLOCK -> Optional.of("__BLOCKED__");
                    case LOWERCASE -> Optional.of(message);
                };
            }
        }
        return Optional.empty();
    }
}
