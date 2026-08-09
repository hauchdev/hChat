package dev.hauch.hchat.api.premium;

import org.bukkit.entity.Player;

/**
 * Extension point applied by {@code ChatListener} while a chat message is
 * being processed.
 * <p>
 * The free edition ships with no enhancers, so every default method here is
 * a no-op. Premium editions provide enhancers to block, transform or
 * decorate messages without forking the free core code. Enhancers are
 * invoked in registration order.
 */
public interface PremiumChatEnhancer {

    /**
     * Called while the free hChat checks run, after the channel alias is
     * stripped but before the word filter.
     *
     * @param sender  the player who typed the message
     * @param message the plain text message
     * @return {@code false} to cancel the message entirely. The
     *         implementation is responsible for telling the player why
     *         (message, sound, ...).
     */
    default boolean allowChat(Player sender, String message) {
        return true;
    }

    /**
     * Called on the plain message text before it is rendered. Return the
     * transformed text (emojis, per-permission colors, ...).
     *
     * @param sender  the player who typed the message
     * @param message the current plain text message
     * @return the message text to use from now on
     */
    default String transformMessage(Player sender, String message) {
        return message;
    }

    /**
     * Called on the resolved chat format string before it is rendered.
     * The format already has {@code {prefix}}, {@code {suffix}},
     * {@code {player}} and PlaceholderAPI placeholders resolved.
     *
     * @param sender the player who typed the message
     * @param format the resolved format string
     * @return the format string to use from now on
     */
    default String transformFormat(Player sender, String format) {
        return format;
    }
}
