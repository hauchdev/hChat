package dev.hauch.hchat.bootstrap;

import dev.hauch.hchat.api.platform.Platform;
import dev.hauch.hchat.api.platform.PlatformAdapter;
import dev.hauch.hchat.exception.PlatformLoadException;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.logging.Logger;

// class VersionLoader
public final class VersionLoader {

    public static final String MODERN_ALIAS = "v1_21_R1";

    private static final Map<String, String> REVISION_TABLE = Map.ofEntries(
            
            Map.entry("1.18.2", "v1_18_R2"),

            Map.entry("1.19.1", "v1_19_R1"),
            Map.entry("1.19.2", "v1_19_R1"),
            Map.entry("1.19.3", "v1_19_R2"),
            Map.entry("1.19.4", "v1_19_R3"),

            Map.entry("1.20.1", "v1_20_R1"),
            Map.entry("1.20.2", "v1_20_R2"),
            Map.entry("1.20.3", "v1_20_R3"),
            Map.entry("1.20.4", "v1_20_R3"),
            Map.entry("1.20.5", "v1_20_R4"),
            Map.entry("1.20.6", "v1_20_R4"),

            Map.entry("1.21.1", "v1_21_R1"),
            Map.entry("1.21.2", "v1_21_R1"),
            Map.entry("1.21.3", "v1_21_R1"),
            Map.entry("1.21.4", "v1_21_R1"),
            Map.entry("1.21.5", "v1_21_R2"),
            Map.entry("1.21.6", "v1_21_R3"),
            Map.entry("1.21.7", "v1_21_R3"),
            Map.entry("1.21.8", "v1_21_R3")
    );

    private static final Map<String, String> LATEST_FOR_PREFIX = Map.of(
            "1.18", "v1_18_R2",
            "1.19", "v1_19_R3",
            "1.20", "v1_20_R4",
            "1.21", "v1_21_R3"
    );

    // bootstrap
    // bootstrap data
    public static PlatformAdapter bootstrap(Logger logger) {
        String revision = detectViaPackage();
        if (revision == null) {
            revision = detectViaMinecraftVersion(logger);
        }
        if (revision == null) {
            throw new PlatformLoadException(
                    "Could not determine the running Minecraft version.");
        }

        String fqn = "dev.hauch.hchat." + revision + ".PlatformAdapter_" + revision;
        logger.info("[VersionLoader] Loading PlatformAdapter: " + fqn);
        PlatformAdapter adapter = instantiate(fqn, logger);
        Platform.set(adapter);
        return adapter;
    }

    // detect via package
    private static String detectViaPackage() {
        try {
            String pkg = Bukkit.getServer().getClass().getPackage().getName();
            int idx = pkg.lastIndexOf(".v");
            if (idx < 0) return null;
            return pkg.substring(idx + 1).trim();
        } catch (NullPointerException | IllegalArgumentException ignored) {
            return null;
        }
    }

    // detect via minecraft version
    private static String detectViaMinecraftVersion(Logger logger) {
        try {
            String mc = Bukkit.getMinecraftVersion();
            if (mc == null || mc.isBlank()) {
                logger.warning("[VersionLoader] Bukkit.getMinecraftVersion()"
                        + " returned an empty value - cannot resolve a"
                        + " revision id.");
                return null;
            }
            KnownUnknown lookup = resolveRevisionWithOrigin(mc);
            return switch (lookup) {
                case KnownUnknown.RESOLVED(String r) -> r;
                case KnownUnknown.FUTURE_FALLBACK(String r) -> {
                    logger.warning("[VersionLoader] Minecraft version '"
                            + mc + "' has no explicit revision entry."
                            + " Falling back to the modern alias '" + r
                            + "'. This server is running on an UNTESTED"
                            + " Paper build - please open an issue so we"
                            + " can add a proper v<rev> Maven module.");
                    yield r;
                }
                case KnownUnknown.UNKNOWN u -> null;
            };
        } catch (RuntimeException ignored) {
            return null;
        }
    }
    // interface KnownUnknown
    sealed interface KnownUnknown {
        // record RESOLVED
        record RESOLVED(String revision) implements KnownUnknown {}
        // record FUTURE_FALLBACK
        record FUTURE_FALLBACK(String revision) implements KnownUnknown {}
        // record UNKNOWN
        record UNKNOWN() implements KnownUnknown {}
    }

    // get revision with origin
    static KnownUnknown resolveRevisionWithOrigin(String mcVersion) {
        if (mcVersion == null || mcVersion.isBlank()) return new KnownUnknown.UNKNOWN();

        String exact = REVISION_TABLE.get(mcVersion);
        if (exact != null) return new KnownUnknown.RESOLVED(exact);

        String majorMinor = mcVersion.length() >= 4
                ? mcVersion.substring(0, 4)
                : mcVersion;
        String byPrefix = REVISION_TABLE.get(majorMinor);
        if (byPrefix != null) return new KnownUnknown.RESOLVED(byPrefix);

        String latest = LATEST_FOR_PREFIX.get(majorMinor);
        if (latest != null) return new KnownUnknown.RESOLVED(latest);

        if (mcVersion.startsWith("2") || mcVersion.startsWith("3")) {
            return new KnownUnknown.FUTURE_FALLBACK(MODERN_ALIAS);
        }
        return new KnownUnknown.UNKNOWN();
    }

    // get revision
    static String resolveRevision(String mcVersion) {
        return switch (resolveRevisionWithOrigin(mcVersion)) {
            case KnownUnknown.RESOLVED(String r) -> r;
            case KnownUnknown.FUTURE_FALLBACK(String r) -> r;
            case KnownUnknown.UNKNOWN u -> null;
        };
    }

    // instantiate data
    private static PlatformAdapter instantiate(String fqn, Logger logger) {
        try {
            Class<?> clazz = Class.forName(fqn);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            if (!(instance instanceof PlatformAdapter adapter)) {
                throw new PlatformLoadException(
                        "Class " + fqn + " is not a PlatformAdapter");
            }
            return adapter;
        } catch (PlatformLoadException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            throw new PlatformLoadException(
                    "No PlatformAdapter shipped for this Minecraft version. "
                            + "Expected class: " + fqn
                            + ". Edit the REVISION_TABLE in VersionLoader"
                            + " or add a new v<rev> Maven module.", e);
        } catch (Throwable t) {
            throw new PlatformLoadException(
                    "Failed to load PlatformAdapter: " + fqn, t);
        }
    }

    private VersionLoader() {
        throw new UnsupportedOperationException("Utility class");
    }
}
