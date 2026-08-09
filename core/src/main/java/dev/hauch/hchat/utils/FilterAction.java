package dev.hauch.hchat.utils;

// enum FilterAction
public enum FilterAction {

    /** Cancel the message entirely. */
    BLOCK,
    /** Replace the offending part (or the whole message) with asterisks. */
    MASK,
    /** Block the message and notify staff with hchat.monitor.filter. */
    WARN,
    /** Transform the message to lowercase (used by anti-caps). */
    LOWERCASE;

    // parse data
    public static FilterAction parse(String raw, FilterAction fallback) {
        if (raw == null) return fallback;
        try {
            return valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
}
