package dev.hauch.hchat.utils;

import java.util.Locale;

/**
 * How format strings are parsed before being shown to players.
 *
 * <ul>
 *   <li>{@link #LEGACY} - classic {@code &} codes plus {@code &#RRGGBB} hex
 *       (the historical hChat behavior, default for existing installs).</li>
 *   <li>{@link #MINIMESSAGE} - Adventure
 *       <a href="https://docs.advntr.dev/minimessage">MiniMessage</a> tags
 *       ({@code <red>}, {@code <gradient:...>}, {@code <#RRGGBB>}); classic
 *       {@code &} codes and {@code &#RRGGBB} hex are still translated so
 *       existing formats keep working.</li>
 *   <li>{@link #AUTO} - legacy parsing when the string contains {@code &}
 *       codes, MiniMessage otherwise.</li>
 * </ul>
 */
public enum FormatMode {

    LEGACY,
    MINIMESSAGE,
    AUTO;

    // parse data
    public static FormatMode parse(String raw) {
        if (raw == null) return LEGACY;
        return switch (raw.trim().toLowerCase(Locale.ROOT)) {
            case "minimessage", "mm", "mini" -> MINIMESSAGE;
            case "auto" -> AUTO;
            default -> LEGACY;
        };
    }
}
