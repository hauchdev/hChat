package dev.hauch.hchat.api.platform;

import dev.hauch.hchat.exception.PlatformLoadException;

// class Platform
public final class Platform {

    private static volatile PlatformAdapter adapter;

    // make Platform
    private Platform() {
        throw new UnsupportedOperationException("Utility class");
    }

    // get data
    public static PlatformAdapter get() {
        PlatformAdapter instance = adapter;
        if (instance == null) {
            throw new PlatformLoadException(
                    "Platform adapter has not been loaded yet. "
                            + "Did Bootstrap.initialize() run?");
        }
        return instance;
    }

    // set data
    public static void set(PlatformAdapter instance) {
        if (instance == null) {
            throw new IllegalArgumentException("PlatformAdapter cannot be null");
        }
        adapter = instance;
    }

    // id data
    public static String id() {
        return get().getId();
    }

    // reset data
    public static void reset() {
        adapter = null;
    }
}
