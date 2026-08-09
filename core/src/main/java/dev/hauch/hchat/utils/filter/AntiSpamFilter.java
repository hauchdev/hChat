package dev.hauch.hchat.utils.filter;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.utils.FilterAction;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Two-stage anti-spam: a global per-player cooldown and a sliding-window
 * flood check (more than {@code max-messages} messages within
 * {@code window-seconds}). Messages that are near-identical to the player's
 * previous message are also flagged while inside the window.
 * <p>
 * Note: the filter records the attempt on every check, so a blocked message
 * still counts against the player - which is the desired behaviour for
 * spam prevention.
 */
public final class AntiSpamFilter implements ChatFilter {

    private final PluginConfig config;

    private final Map<UUID, Long> lastMessage = new ConcurrentHashMap<>();
    private final Map<UUID, String> lastText = new ConcurrentHashMap<>();
    private final Map<UUID, Deque<Long>> history = new ConcurrentHashMap<>();

    // make AntiSpamFilter
    public AntiSpamFilter(PluginConfig config) {
        this.config = config;
    }

    @Override
    public boolean isEnabled() {
        return config.isAntiSpamEnabled();
    }

    @Override
    public FilterAction action() {
        return config.getAntiSpamAction();
    }

    @Override
    public Optional<String> check(Player player, String message) {
        if (!isEnabled() || message == null || message.isBlank()) {
            return Optional.empty();
        }

        UUID id = player.getUniqueId();
        long now = System.currentTimeMillis();

        // 1. global per-player cooldown
        Long last = lastMessage.get(id);
        if (last != null && now - last < config.getAntiSpamGlobalCooldownMs()) {
            return Optional.of("__BLOCKED__");
        }

        // 2. sliding-window flood detection
        Deque<Long> window = history.computeIfAbsent(id, k -> new ArrayDeque<>());
        synchronized (window) {
            while (!window.isEmpty()
                    && now - window.peekFirst() > config.getAntiSpamFloodWindowMs()) {
                window.pollFirst();
            }
            window.addLast(now);
            if (window.size() > config.getAntiSpamFloodMaxMessages()) {
                return Optional.of("__BLOCKED__");
            }

            // 3. repeated near-identical message inside the window
            String previous = lastText.get(id);
            if (previous != null
                    && window.size() >= 2
                    && similar(previous, message, config.getAntiSpamFloodSimilarity())) {
                return Optional.of("__BLOCKED__");
            }
            lastText.put(id, message);
        }

        lastMessage.put(id, now);
        return Optional.empty();
    }

    // normalized similarity in [0,1]; 1 = identical
    private static boolean similar(String a, String b, double threshold) {
        String na = normalize(a);
        String nb = normalize(b);
        int maxLen = Math.max(na.length(), nb.length());
        if (maxLen == 0) return true;
        double ratio = 1.0 - (double) levenshtein(na, nb) / maxLen;
        return ratio >= threshold;
    }

    // lowercased, spaces collapsed - two messages with different spacing match
    private static String normalize(String text) {
        return text.toLowerCase(java.util.Locale.ROOT).replaceAll("\\s+", " ").trim();
    }

    // iterative Levenshtein distance
    private static int levenshtein(String a, String b) {
        int[] prev = new int[b.length() + 1];
        int[] curr = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) prev[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            curr[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                curr[j] = Math.min(Math.min(curr[j - 1] + 1, prev[j] + 1),
                        prev[j - 1] + cost);
            }
            int[] tmp = prev;
            prev = curr;
            curr = tmp;
        }
        return prev[b.length()];
    }
}
