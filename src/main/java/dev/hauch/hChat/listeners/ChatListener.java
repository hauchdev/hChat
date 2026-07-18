package dev.hauch.hChat.listeners;

import dev.hauch.hChat.HChat;
import dev.hauch.hChat.utils.MessageFormatter;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

    private final HChat plugin;
    private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w+)");

    public ChatListener(HChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();

        String plainMessage = PlainTextComponentSerializer.plainText()
                .serialize(event.message());

        dev.hauch.hChat.utils.WordFilter wordFilter =
                new dev.hauch.hChat.utils.WordFilter(plugin.getConfigManager());
        if (wordFilter.isEnabled()) {
            Optional<String> result = wordFilter.filter(plainMessage);
            if (result.isPresent()) {
                String r = result.get();
                if ("__BLOCKED__".equals(r)) {
                    String msg = plugin.getMessages().getString("message-filtered");
                    sender.sendMessage(dev.hauch.hChat.utils.MessageFormatter.format(msg));
                    event.setCancelled(true);
                    return;
                } else {
                    event.message(MessageFormatter.format(r));
                    plainMessage = r;
                    if (wordFilter.getAction() == dev.hauch.hChat.utils.WordFilter.Action.WARN) {
                        notifyStaff(sender, plainMessage);
                    }
                }
            }
        }

        if (plugin.getConfigManager().isHoverTextEnabled()) {
            Component original = event.message();
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player_name", sender.getName());

            Component hover = MessageFormatter.formatHover(
                    plugin.getConfigManager().getHoverTextFormat(), placeholders);

            event.renderer((source, sourceDisplayName, message, viewer) -> {
                Component displayWithHover = sourceDisplayName.hoverEvent(
                        net.kyori.adventure.text.event.HoverEvent.showText(hover));
                return Component.text("")
                        .append(displayWithHover)
                        .append(Component.text(": "))
                        .append(message);
            });
        }

        Matcher matcher = MENTION_PATTERN.matcher(plainMessage);
        boolean foundMention = false;

        while (matcher.find()) {
            String mentionedName = matcher.group(1);
            Player mentioned = Bukkit.getPlayerExact(mentionedName);

            if (mentioned != null && mentioned.isOnline() && !mentioned.equals(sender)) {
                foundMention = true;

                if (plugin.getConfigManager().isMentionSoundEnabled()) {
                    try {
                        Sound sound = Sound.valueOf(
                                plugin.getConfigManager().getMentionSound().toUpperCase());
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            mentioned.playSound(
                                    mentioned.getLocation(),
                                    sound,
                                    plugin.getConfigManager().getMentionSoundVolume(),
                                    plugin.getConfigManager().getMentionSoundPitch()
                            );
                        });
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid mention sound: "
                                + plugin.getConfigManager().getMentionSound());
                    }
                }

                if (plugin.getConfigManager().isMentionColorsEnabled()) {
                    Map<String, String> mentionPlaceholders = new HashMap<>();
                    mentionPlaceholders.put("player", sender.getName());
                    mentioned.sendMessage(MessageFormatter.format(
                            plugin.getMessages().getString("mentioned"),
                            mentionPlaceholders));
                }
            }
        }

        if (foundMention && plugin.getConfigManager().isMentionColorsEnabled()) {
            String mentionColor = plugin.getConfigManager().getMentionColor();
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

    private void notifyStaff(Player sender, String message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("hchat.monitor.filter") && !p.equals(sender)) {
                p.sendMessage(MessageFormatter.format(
                                plugin.getMessages().getString("filter-warn-staff"))
                        .replaceText(b -> b.matchLiteral("{player}").replacement(sender.getName()))
                        .replaceText(b -> b.matchLiteral("{message}").replacement(message)));
            }
        }
    }
}