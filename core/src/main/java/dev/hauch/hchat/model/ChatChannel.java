package dev.hauch.hchat.model;

import dev.hauch.hchat.utils.FormatMode;

/**
 * A chat channel loaded from the {@code channels} section of config.yml.
 *
 * <p>Every channel carries its own format, an optional distance range
 * ( {@code -1} = no limit ), optional speak/see permissions, an optional
 * cooldown and an optional chat alias prefix ( e.g. {@code #staff} ).
 * A channel may also override the global format parse mode with
 * {@code format-mode} ( legacy | minimessage | auto ).</p>
 */
public record ChatChannel(String id,
                          String format,
                          int range,
                          String actionBarHint,
                          String speakPermission,
                          String seePermission,
                          long cooldownMs,
                          String alias,
                          boolean perWorld,
                          FormatMode formatMode) {

    /** Backwards-compatible constructor: legacy format parsing. */
    public ChatChannel(String id,
                       String format,
                       int range,
                       String actionBarHint,
                       String speakPermission,
                       String seePermission,
                       long cooldownMs,
                       String alias,
                       boolean perWorld) {
        this(id, format, range, actionBarHint, speakPermission,
                seePermission, cooldownMs, alias, perWorld, FormatMode.LEGACY);
    }

    /** Never-null parse mode for this channel (LEGACY when unset). */
    public FormatMode effectiveFormatMode() {
        return formatMode == null ? FormatMode.LEGACY : formatMode;
    }

    // has explicit format
    public boolean hasFormat() {
        return format != null && !format.isBlank();
    }

    // has action bar hint
    public boolean hasActionBarHint() {
        return actionBarHint != null && !actionBarHint.isBlank();
    }

    // has speak permission
    public boolean hasSpeakPermission() {
        return speakPermission != null && !speakPermission.isBlank();
    }

    // has see permission
    public boolean hasSeePermission() {
        return seePermission != null && !seePermission.isBlank();
    }

    // has alias
    public boolean hasAlias() {
        return alias != null && !alias.isBlank();
    }

    // is unlimited range
    public boolean isUnlimited() {
        return range < 0;
    }

    // is world-scoped (messages never cross worlds)
    public boolean hasPerWorld() {
        return perWorld;
    }
}
