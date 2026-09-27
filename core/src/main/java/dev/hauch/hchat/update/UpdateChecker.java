package dev.hauch.hchat.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.utils.MessageFormatter;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Checks the latest Modrinth version of this plugin against the running
 * version via the public Modrinth API. Runs fully async (never blocks the
 * main thread) and caches the result so listeners can read it later.
 */
public final class UpdateChecker {

    private static final String API_BASE = "https://api.modrinth.com/v2/project/";
    private static final String SITE_BASE = "https://modrinth.com/plugin/";
    private static final String DEFAULT_SLUG = "hchat";

    private final Plugin plugin;
    private final String slug;           // "hchat"
    private final String currentVersion; // "1.2.3"

    private volatile boolean checked;
    private volatile String latestVersion;
    private volatile String releaseUrl;
    private volatile boolean updateAvailable;

    public UpdateChecker(Plugin plugin) {
        this(plugin, DEFAULT_SLUG);
    }

    public UpdateChecker(Plugin plugin, String slug) {
        this.plugin = plugin;
        String configured = slug == null || slug.isBlank() ? DEFAULT_SLUG : slug.trim();
        // allow pasting a full modrinth.com URL instead of the bare slug
        if (configured.contains("modrinth.com/")) {
            String tail = configured.substring(configured.indexOf("modrinth.com/")
                    + "modrinth.com/".length());
            int slash = tail.indexOf('/');
            configured = (slash > 0 ? tail.substring(0, slash) : tail).trim();
        }
        this.slug = configured;
        this.currentVersion = plugin.getDescription().getVersion();
    }

    // configured modrinth project slug
    public String projectSlug() {
        return slug;
    }

    // current running version
    public String currentVersion() {
        return currentVersion;
    }

    // was the check already executed
    public boolean isChecked() {
        return checked;
    }

    // latest version found on Modrinth (null if the check failed)
    public String latestVersion() {
        return latestVersion;
    }

    // url of the Modrinth project page
    public String releaseUrl() {
        return releaseUrl;
    }

    // is there a newer version available
    public boolean isUpdateAvailable() {
        return checked && updateAvailable;
    }

    // run the check on a background thread; onComplete runs on the main thread
    public void checkAsync(Runnable onComplete) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                check();
            } finally {
                if (onComplete != null) {
                    try {
                        plugin.getServer().getScheduler().runTask(plugin, onComplete);
                    } catch (IllegalStateException ignored) {
                        // plugin is being disabled - nothing to schedule on
                    }
                }
            }
        });
    }

    // perform the HTTP request and cache the result
    private void check() {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + slug + "/version"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .header("User-Agent", "hChat/" + currentVersion)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                plugin.getLogger().warning("[UpdateChecker] Modrinth project '"
                        + slug + "' not found - check update-checker.project in config.yml");
                return;
            }
            if (response.statusCode() != 200) {
                plugin.getLogger().warning("[UpdateChecker] Modrinth API returned HTTP "
                        + response.statusCode());
                return;
            }

            JsonArray versions = JsonParser.parseString(response.body()).getAsJsonArray();
            String versionNumber = null;
            for (JsonElement element : versions) {
                JsonObject version = element.getAsJsonObject();
                // the API lists newest first; skip channels (alpha/beta) so
                // players only get pinged for stable releases
                if (version.has("version_type")
                        && !"release".equals(version.get("version_type").getAsString())) {
                    continue;
                }
                versionNumber = version.has("version_number")
                        ? version.get("version_number").getAsString() : null;
                break;
            }

            if (versionNumber == null) return;

            latestVersion = normalize(versionNumber);
            releaseUrl = SITE_BASE + slug;
            updateAvailable = compare(currentVersion, latestVersion) < 0;
            checked = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            plugin.getLogger().log(Level.WARNING,
                    "[UpdateChecker] Update check interrupted: {0}", e.getMessage());
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING,
                    "[UpdateChecker] Failed to check for updates: {0}",
                    e.getMessage());
        } catch (RuntimeException e) {
            plugin.getLogger().log(Level.WARNING,
                    "[UpdateChecker] Unexpected error: {0}", e.getMessage());
        }
    }

    // build the clickable "update available" message
    public Component updateMessage(PluginMessages messages) {
        Map<String, String> ph = new HashMap<>();
        ph.put("current", currentVersion);
        ph.put("latest", latestVersion);
        return MessageFormatter.withOpenUrl(
                MessageFormatter.format(messages.getString("update-available"), ph),
                releaseUrl);
    }

    // "v1.2.3" -> "1.2.3"
    private static String normalize(String version) {
        return version.replaceFirst("^[vV]", "").trim();
    }

    /**
     * Compare two versions like "1.2.3" or "1.2.3-RC1" as dot-separated
     * integers. Pre-release suffixes ("-rc1", "-beta") are ignored, so
     * they never rank above the stable release. Returns a negative
     * number when current < latest.
     */
    private static int compare(String current, String latest) {
        int[] a = parse(current);
        int[] b = parse(latest);
        int length = Math.max(a.length, b.length);
        for (int i = 0; i < length; i++) {
            int ai = i < a.length ? a[i] : 0;
            int bi = i < b.length ? b[i] : 0;
            if (ai != bi) return Integer.compare(ai, bi);
        }
        return 0;
    }

    // "1.2.3-rc1" -> [1, 2, 3]: only the numeric dot-separated prefix counts
    private static int[] parse(String version) {
        String[] token = version.split("[^0-9.]");
        String prefix = token.length > 0 ? token[0] : "";
        String[] parts = prefix.split("\\.");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                result[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                result[i] = 0;
            }
        }
        return result;
    }
}
