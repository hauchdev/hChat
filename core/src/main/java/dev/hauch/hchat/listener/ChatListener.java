package dev.hauch.hchat.listener;

import dev.hauch.hchat.api.platform.SoundLookup;
import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.utils.MessageFormatter;
import dev.hauch.hchat.utils.WordFilter;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// class ChatListener
public class ChatListener implements Listener {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w+)");

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;
    private final WordFilter wordFilter;

    // make ChatListener
    public ChatListener(Plugin plugin,
                        PluginConfig config,
                        PluginMessages messages,
                        WordFilter wordFilter) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.wordFilter = wordFilter;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    // on chat
    public void onChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();

        String plainMessage = PlainTextComponentSerializer.plainText()
                .serialize(event.message());

        if (wordFilter.isEnabled()) {
            Optional<String> result = wordFilter.filter(plainMessage);
            if (result.isPresent()) {
                String r = result.get();
                if ("__BLOCKED__".equals(r)) {
                    sender.sendMessage(MessageFormatter.format(messages.getString("message-filtered")));
                    event.setCancelled(true);
                    return;
                } else {
                    event.message(MessageFormatter.format(r));
                    plainMessage = r;
                    if (wordFilter.getAction() == WordFilter.Action.WARN) {
                        notifyStaff(sender, plainMessage);
                    }
                }
            }
        }

        String format = config.resolveChatFormat(sender);

        event.renderer((source, sourceDisplayName, message, viewer) -> {
            String resolved = format
                    .replace("{prefix}", "%vault_prefix%")
                    .replace("{suffix}", "%vault_suffix%")
                    .replace("{player}", source.getName())
                    .replace("{message}", PlainTextComponentSerializer.plainText().serialize(message));
            try {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    resolved = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(source, resolved);
                }
                
            } catch (Throwable placeholderFailure) {
                plugin.getLogger().warning(
                        "[ChatListener] PlaceholderAPI substitution failed"
                                + " for player '" + source.getName() + "': "
                                + placeholderFailure.getMessage());
            }

            return MessageFormatter.format(resolved);
        });

        Matcher matcher = MENTION_PATTERN.matcher(plainMessage);
        boolean foundMention = false;
        while (matcher.find()) {
            String mentionedName = matcher.group(1);
            Player mentioned = Bukkit.getPlayerExact(mentionedName);
            if (mentioned != null && mentioned.isOnline() && !mentioned.equals(sender)) {
                foundMention = true;
                mentionsNotify(mentioned, sender);
            }
        }

        if (foundMention && config.isMentionColorsEnabled()) {
            String mentionColor = config.getMentionColor();
            Component newMessage = event.message();
            newMessage = newMessage.replaceText(builder -> {
                builder.match(MENTION_PATTERN);
                builder.replacement((matchResult, input) -> {
                    String mention = matchResult.group();
                    String name = matchResult.group(1);
                    Player mentioned = Bukkit.getPlayerExact(name);
                    if (mentioned != null && mentioned.isOnline()) {
                        return MessageFormatter.format(mentionColor + mention);
                    }
                    return Component.text(mention);
                });
            });
            event.message(newMessage);
        }
    }

    // mentions notify
    private void mentionsNotify(Player mentioned, Player sender) {
        if (config.isMentionSoundEnabled()) {
            try {
                Sound sound = SoundLookup.resolve(
                        config.getMentionSound());
                Bukkit.getScheduler().runTask(plugin, () -> mentioned.playSound(
                        mentioned.getLocation(), sound,
                        config.getMentionSoundVolume(),
                        config.getMentionSoundPitch()));
            } catch (IllegalArgumentException ignored) { }
        }
        if (config.isMentionColorsEnabled()) {
            Map<String, String> ph = new HashMap<>();
            ph.put("player", sender.getName());
            mentioned.sendMessage(MessageFormatter.format(
                    messages.getString("mentioned"), ph));
        }
    }

    // notify staff
    private void notifyStaff(Player sender, String message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("hchat.monitor.filter") && !p.equals(sender)) {
                p.sendMessage(MessageFormatter.format(messages.getString("filter-warn-staff"))
                        .replaceText(b -> b.matchLiteral("{player}").replacement(sender.getName()))
                        .replaceText(b -> b.matchLiteral("{message}").replacement(message)));
            }
        }
    }
}
