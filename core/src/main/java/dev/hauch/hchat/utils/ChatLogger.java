package dev.hauch.hchat.utils;

import dev.hauch.hchat.config.PluginConfig;
import org.bukkit.plugin.Plugin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.stream.Stream;

// class ChatLogger
public class ChatLogger {

    private final Plugin plugin;
    private final boolean enabled;
    private final Path logsDir;
    private final int retentionDays;

    // make ChatLogger
    public ChatLogger(Plugin plugin, PluginConfig config) {
        this.plugin = plugin;
        this.enabled = config.isPrivateMessageLoggingEnabled();
        this.logsDir = plugin.getDataFolder().toPath()
                .resolve(config.getLoggingPath());
        this.retentionDays = config.getLoggingRetentionDays();
    }

    // is enabled
    public boolean isEnabled() {
        return enabled;
    }

    // logs directory
    public Path logsDirectory() {
        return logsDir;
    }

    // log private message
    public void logPrivateMessage(String sender, String receiver, String message) {
        if (!enabled) return;
        try {
            if (!Files.exists(logsDir)) Files.createDirectories(logsDir);
            Path file = logsDir.resolve(LocalDate.now() + ".log");

            String line = String.format("[%s] %s -> %s: %s",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    sender, receiver, message);

            try (BufferedWriter writer = Files.newBufferedWriter(file,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING,
                    "Failed to log private message: {0}", e.getMessage());
        }
    }

    // purge old logs
    public void purgeOldLogs() {
        if (retentionDays <= 0 || !Files.exists(logsDir)) return;
        try (Stream<Path> stream = Files.list(logsDir)) {
            stream.filter(p -> p.getFileName().toString().endsWith(".log"))
                    .forEach(p -> {
                        try {
                            String name = p.getFileName().toString().replace(".log", "");
                            long age = ChronoUnit.DAYS.between(LocalDate.parse(name), LocalDate.now());
                            if (age > retentionDays) Files.deleteIfExists(p);
                        } catch (Exception ignored) {}
                    });
        } catch (IOException ignored) {}
    }
}