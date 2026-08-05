package dev.hauch.hchat.model;

/**
 * A chat channel loaded from the {@code channels} section of config.yml.
 *
 * <p>Every channel carries its own format, an optional distance range
 * ( {@code -1} = no limit ), optional speak/see permissions, an optional
 * cooldown and an optional chat alias prefix ( e.g. {@code #staff} ).</p>
 */
public record ChatChannel(String id,
                          String format,
                          int range,
                          String actionBarHint,
                          String speakPermission,
                          String seePermission,
                          long cooldownMs,
                          String alias) {

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
}
