package dev.hauch.hchat.utils;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.utils.filter.ChatFilter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

// class WordFilter
public class WordFilter implements ChatFilter {

    private final PluginConfig config;
    private final List<Pattern> patterns;
    private final FilterAction action;

    public WordFilter(PluginConfig config) {
        this.config = config;
        this.patterns = new ArrayList<>();
        this.action = FilterAction.parse(config.getWordFilterAction(), FilterAction.BLOCK);
        if (!config.isWordFilterEnabled()) return;
        for (String word : config.getWordFilterWords()) {
            if (word == null || word.isBlank()) continue;
            patterns.add(Pattern.compile("(?i)\\b" + Pattern.quote(word) + "\\b"));
        }
    }

    // is enabled
    public boolean isEnabled() { return !patterns.isEmpty(); }

    // get action
    @Override
    public FilterAction action() { return action; }

    // filter data
    @Override
    public Optional<String> check(Player player, String message) {
        if (!isEnabled() || message == null) return Optional.empty();
        for (Pattern p : patterns) {
            if (p.matcher(message).find()) {
                if (action == FilterAction.MASK) {
                    return Optional.of(p.matcher(message).replaceAll(
                            m -> "*".repeat(m.group().length())));
                }
                return Optional.of("__BLOCKED__");
            }
        }
        return Optional.empty();
    }
}