package dev.hauch.hChat.utils;

import dev.hauch.hChat.config.PluginConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class WordFilter {

    public enum Action { BLOCK, MASK, WARN }

    private final PluginConfig config;
    private final List<Pattern> patterns;
    private final Action action;

    public WordFilter(PluginConfig config) {
        this.config = config;
        this.patterns = new ArrayList<>();
        this.action = parseAction(config.getWordFilterAction());
        if (!config.isWordFilterEnabled()) return;
        for (String word : config.getWordFilterWords()) {
            if (word == null || word.isBlank()) continue;
            patterns.add(Pattern.compile("(?i)\\b" + Pattern.quote(word) + "\\b"));
        }
    }

    public boolean isEnabled() { return !patterns.isEmpty(); }

    public Action getAction() { return action; }

    public Optional<String> filter(String message) {
        if (!isEnabled() || message == null) return Optional.empty();
        for (Pattern p : patterns) {
            if (p.matcher(message).find()) {
                if (action == Action.MASK) {
                    return Optional.of(p.matcher(message).replaceAll(
                            m -> "*".repeat(m.group().length())));
                }
                return Optional.of("__BLOCKED__");
            }
        }
        return Optional.empty();
    }

    private static Action parseAction(String raw) {
        if (raw == null) return Action.BLOCK;
        try { return Action.valueOf(raw.trim().toUpperCase()); }
        catch (IllegalArgumentException e) { return Action.BLOCK; }
    }
}