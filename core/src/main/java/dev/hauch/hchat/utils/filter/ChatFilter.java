package dev.hauch.hchat.utils.filter;

import dev.hauch.hchat.utils.FilterAction;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * A single chat filter in the {@link FilterChain}. The contract mirrors the
 * legacy {@code WordFilter}: {@link #check} returns
 * <ul>
 *   <li>{@link Optional#empty()} when the message passes,</li>
 *   <li>{@code "__BLOCKED__"} when it must be cancelled,</li>
 *   <li>otherwise the replacement text (masked / lowercased).</li>
 * </ul>
 */
public interface ChatFilter {

    // is enabled
    boolean isEnabled();

    // action applied when the filter fires
    FilterAction action();

    // check the message (see class javadoc for the return contract)
    Optional<String> check(Player player, String message);
}
