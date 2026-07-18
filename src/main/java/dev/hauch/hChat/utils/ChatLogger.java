package dev.hauch.hChat.utils;

import dev.hauch.hChat.HChat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

public class ChatLogger {

    private final HChat plugin;
    private final boolean enabled;
    private final Path logsDir;
    private final int retentionDays;

    public ChatLogger(HChat plugin) {
        this.plugin = plugin;
        this.enabled = plugin.getConfigManager().isPrivateMessageLoggingEnabled();
        this.logsDir = plugin.getDataFolder().toPath()
                .resolve(plugin.getConfigManager().getLoggingPath());
        this.retentionDays = plugin.getConfigManager().getLoggingRetentionDays();
    }

    public void logPrivateMessage(String sender, String receiver, String message) {
        if (!enabled) return;
        try {
            if (!Files.exists(logsDir)) Files.createDirectories(logsDir);
            Path file = logsDir.resolve(LocalDate.now() + ".log");

            String line = String.format("[%s] %s -> %s: %s",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    sender, receiver, message);

            try (BufferedWriter w = Files.newBufferedWriter(file,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                w.write(line);
                w.newLine();
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to log private message: " + e.getMessage());
        }
    }

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