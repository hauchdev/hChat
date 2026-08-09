package dev.hauch.hchat.utils.filter;

import dev.hauch.hchat.utils.FilterAction;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

/**
 * Runs every registered {@link ChatFilter} in order. The first filter that
 * reacts decides the outcome, so the ordering chosen at bootstrap matters
 * (normalize/transform filters like anti-caps run after blocking filters).
 */
public final class FilterChain {

    /** Outcome of running the chain: the replacement text plus the filter that produced it. */
    public record Result(String text, ChatFilter source) {

        // is the message blocked
        public boolean blocked() {
            return "__BLOCKED__".equals(text);
        }

        // did the source filter warn (block + notify staff)
        public boolean warned() {
            return source != null && source.action() == FilterAction.WARN;
        }
    }

    private final List<ChatFilter> filters;

    // make FilterChain
    public FilterChain(List<ChatFilter> filters) {
        this.filters = filters == null ? List.of() : List.copyOf(filters);
    }

    // is enabled (any filter active)
    public boolean isEnabled() {
        for (ChatFilter filter : filters) {
            if (filter.isEnabled()) return true;
        }
        return false;
    }

    // run the chain
    public Optional<Result> filter(Player player, String message) {
        for (ChatFilter filter : filters) {
            if (!filter.isEnabled()) continue;
            Optional<String> result = filter.check(player, message);
            if (result.isPresent()) {
                return Optional.of(new Result(result.get(), filter));
            }
        }
        return Optional.empty();
    }
}
