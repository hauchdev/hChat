package dev.hauch.hchat.api.platform;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;

import java.util.Locale;

// class SoundLookup
public final class SoundLookup {

    // make SoundLookup
    private SoundLookup() {
        throw new UnsupportedOperationException("Utility class");
    }

    // get 
    // resolve data
    public static Sound resolve(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException(
                    "Sound key cannot be null or blank");
        }

        Sound viaRegistry = resolveViaRegistry(key);
        if (viaRegistry != null) return viaRegistry;

        try {
            @SuppressWarnings("deprecation")
            Sound viaValueOf = Sound.valueOf(key.toUpperCase(Locale.ROOT));
            return viaValueOf;
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Cannot resolve sound key '" + key + "' via either"
                            + " Registry.SOUNDS or Sound.valueOf", ex);
        }
    }

    // get via registry
    private static Sound resolveViaRegistry(String key) {
        String path = modernise(key);
        NamespacedKey minecraftKey = NamespacedKey.fromString(
                "minecraft:" + path);
        if (minecraftKey != null) {
            Sound found = Registry.SOUNDS.get(minecraftKey);
            if (found != null) return found;
        }

        NamespacedKey exact = NamespacedKey.fromString(path);
        if (exact != null) {
            Sound found = Registry.SOUNDS.get(exact);
            if (found != null) return found;
        }
        return null;
    }

    // modernise data
    private static String modernise(String key) {
        return key.trim().toLowerCase(Locale.ROOT).replace('_', '.');
    }
}
