package dev.hauch.hchat.utils.filter;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.utils.FilterAction;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Blocks invisible and direction-control Unicode characters commonly used
 * to bypass word filters: zero-width spaces (U+200B-200F), direction
 * overrides (U+202A-202E), word/line joiners (U+2060-206F), the BOM
 * (U+FEFF), soft hyphens (U+00AD) and the tag characters (U+E0000-E0FFF).
 */
public final class AntiUnicodeFilter implements ChatFilter {

    private static final Pattern INVISIBLE = Pattern.compile(
            "[\\u00AD\\u200B-\\u200F\\u202A-\\u202E\\u2060-\\u206F\\uFEFF]"
                    + "|\\uDB40[\\uDC00-\\uDFFF]");

    private final PluginConfig config;

    // make AntiUnicodeFilter
    public AntiUnicodeFilter(PluginConfig config) {
        this.config = config;
    }

    @Override
    public boolean isEnabled() {
        return config.isAntiUnicodeEnabled() && config.isAntiUnicodeBlockInvisible();
    }

    @Override
    public FilterAction action() {
        return config.getAntiUnicodeAction();
    }

    @Override
    public Optional<String> check(Player player, String message) {
        if (!isEnabled() || message == null || message.isEmpty()) {
            return Optional.empty();
        }
        if (!INVISIBLE.matcher(message).find()) {
            return Optional.empty();
        }
        return switch (action()) {
            case MASK -> Optional.of(INVISIBLE.matcher(message)
                    .replaceAll("*"));
            case WARN, BLOCK -> Optional.of("__BLOCKED__");
            case LOWERCASE -> Optional.of(message);
        };
    }
}
