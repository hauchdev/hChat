package dev.hauch.hchat.update;

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
 * Checks the latest GitHub release of this plugin against the running
 * version. Runs fully async (never blocks the main thread) and caches
 * the result so listeners can read it later.
 */
public final class UpdateChecker {

    private static final String DEFAULT_REPO = "hauchdev/hChat";

    private final Plugin plugin;
    private final String repo;           // "hauchdev/hChat"
    private final String currentVersion; // "1.2.3"

    private volatile boolean checked;
    private volatile String latestVersion;
    private volatile String releaseUrl;
    private volatile boolean updateAvailable;

    public UpdateChecker(Plugin plugin) {
        this(plugin, repoFromWebsite(plugin.getDescription().getWebsite()));
    }

    public UpdateChecker(Plugin plugin, String repo) {
        this.plugin = plugin;
        this.repo = repo;
        this.currentVersion = plugin.getDescription().getVersion();
    }

    // derive "owner/repo" from the plugin.yml website URL
    private static String repoFromWebsite(String website) {
        if (website != null && website.contains("github.com/")) {
            String repo = website.substring(
                    website.indexOf("github.com/") + "github.com/".length());
            if (!repo.isEmpty()) return repo;
        }
        return DEFAULT_REPO;
    }

    // current running version
    public String currentVersion() {
        return currentVersion;
    }

    // was the check already executed
    public boolean isChecked() {
        return checked;
    }

    // latest version found on GitHub (null if the check failed)
    public String latestVersion() {
        return latestVersion;
    }

    // url of the latest release page
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
                    .uri(URI.create("https://api.github.com/repos/" + repo + "/releases/latest"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/vnd.github+json")
                    .header("User-Agent", "hChat/" + currentVersion)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                plugin.getLogger().warning("[UpdateChecker] GitHub API returned HTTP "
                        + response.statusCode());
                return;
            }

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            String tag = json.has("tag_name")
                    ? json.get("tag_name").getAsString() : null;
            String htmlUrl = json.has("html_url")
                    ? json.get("html_url").getAsString() : null;

            if (tag == null || htmlUrl == null) return;

            latestVersion = normalize(tag);
            releaseUrl = htmlUrl;
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
