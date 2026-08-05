package dev.hauch.hchat.listener;

import dev.hauch.hchat.api.platform.SoundLookup;
import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.ChannelManager;
import dev.hauch.hchat.model.ChatChannel;
import dev.hauch.hchat.service.DynamicPlaceholderResolver;
import dev.hauch.hchat.utils.MessageFormatter;
import dev.hauch.hchat.utils.WordFilter;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// class ChatListener
public class ChatListener implements Listener {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w+)");

    /** Players outside the channel range but within range * 2 get the action-bar hint. */
    private static final int HINT_RANGE_MULTIPLIER = 2;

    private final Plugin plugin;
    private final PluginConfig config;
    private final PluginMessages messages;
    private final WordFilter wordFilter;
    private final ChannelManager channelManager;
    private final DynamicPlaceholderResolver placeholderResolver;

    // make ChatListener
    public ChatListener(Plugin plugin,
                        PluginConfig config,
                        PluginMessages messages,
                        WordFilter wordFilter,
                        ChannelManager channelManager,
                        DynamicPlaceholderResolver placeholderResolver) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.wordFilter = wordFilter;
        this.channelManager = channelManager;
        this.placeholderResolver = placeholderResolver;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    // on chat
    public void onChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();

        String plainMessage = PlainTextComponentSerializer.plainText()
                .serialize(event.message());

        // 1. resolve the channel (alias prefix -> active channel -> default)
        ChatChannel channel = channelManager.resolveChannel(sender, plainMessage);

        // 2. strip an alias prefix like "#staff" from the visible message.
        //    A message that is *only* the alias has nothing to say.
        if (channel.hasAlias()
                && channelManager.matchesAlias(plainMessage, channel.alias())) {
            String stripped = channelManager.stripAlias(plainMessage, channel.alias());
            if (stripped == null) {
                event.setCancelled(true);
                return;
            }
            plainMessage = stripped;
            event.message(Component.text(stripped));
        }

        // 3. word filter
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

        // 4. speak permission
        if (!channelManager.canSpeak(sender, channel)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("channel-no-access"),
                    Map.of("channel", channel.id())));
            event.setCancelled(true);
            return;
        }

        // 5. per-channel cooldown
        long remainingMs = channelManager.cooldownRemaining(
                sender, "channel:" + channel.id(), channel.cooldownMs());
        if (remainingMs > 0) {
            sender.sendMessage(MessageFormatter.format(messages.getString("channel-cooldown"),
                    Map.of("seconds", String.format(Locale.ROOT, "%.1f", remainingMs / 1000.0),
                            "channel", channel.id())));
            event.setCancelled(true);
            return;
        }
        channelManager.recordCooldown(sender, "channel:" + channel.id());

        // 6. filter who sees the message (see-permission + distance range)
        List<Player> hintTargets = filterViewers(event, sender, channel);

        // 7. action-bar hint for players just outside the range
        if (!hintTargets.isEmpty() && channel.hasActionBarHint()) {
            for (Player nearby : hintTargets) {
                if (channelManager.shouldShowHint(nearby)) {
                    nearby.sendActionBar(MessageFormatter.format(channel.actionBarHint()));
                }
            }
        }

        // 8. per-channel format, clickable to switch channel. The default
        //    channel has no format in config.yml, so it falls back to the
        //    permission-based chat.formats resolution (vip/mvp/admin).
        boolean defaultChannel = channelManager.isDefault(channel);
        String format = channel.hasFormat() ? channel.format()
                : config.resolveChatFormat(sender);

        // The format only depends on the sender, so build it once instead of
        // once per viewer: PAPI, dynamic tokens ([ping] [item] [coords]
        // [world] [afk]) and the message are all resolved before the renderer.
        String resolved = format
                .replace("{prefix}", "%vault_prefix%")
                .replace("{suffix}", "%vault_suffix%")
                .replace("{player}", sender.getName());
        resolved = applyPlaceholders(resolved, sender);

        Component rendered = MessageFormatter.format(resolved);

        // tokens are resolved before {message} is inserted, so tokens typed
        // by players stay literal
        rendered = placeholderResolver.resolve(rendered, sender);

        // {message} is appended last as its own component - color codes and
        // PAPI placeholders inside the message keep working as before
        String messageText = PlainTextComponentSerializer.plainText()
                .serialize(event.message());
        rendered = rendered.replaceText(b -> b.matchLiteral("{message}")
                .replacement(MessageFormatter.format(applyPlaceholders(messageText, sender))));
        Component renderedFormat = rendered;

        event.renderer((source, sourceDisplayName, message, viewer) -> {
            Component line = renderedFormat;
            // let receivers click to join this channel (like NoNChat)
            if (!defaultChannel && viewer instanceof Player v
                    && channelManager.canSee(v, channel)) {
                line = line.hoverEvent(HoverEvent.showText(MessageFormatter.format(
                                messages.getString("channel-click-hover"),
                                Map.of("channel", channel.id()))))
                        .clickEvent(ClickEvent.runCommand("/channel " + channel.id()));
            }
            return line;
        });

        // 9. mentions - only players who can actually see the message
        Set<UUID> recipients = recipients(event);
        Matcher matcher = MENTION_PATTERN.matcher(plainMessage);
        boolean foundMention = false;
        while (matcher.find()) {
            String mentionedName = matcher.group(1);
            Player mentioned = Bukkit.getPlayerExact(mentionedName);
            if (mentioned != null && mentioned.isOnline()
                    && !mentioned.equals(sender)
                    && recipients.contains(mentioned.getUniqueId())) {
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
                    if (mentioned != null && mentioned.isOnline()
                            && recipients.contains(mentioned.getUniqueId())) {
                        return MessageFormatter.format(mentionColor + mention);
                    }
                    return Component.text(mention);
                });
            });
            event.message(newMessage);
        }
    }

    // remove viewers that must not see this message; returns players who
    // are just outside the range and deserve an action-bar hint
    private List<Player> filterViewers(AsyncChatEvent event, Player sender, ChatChannel channel) {
        List<Player> hintTargets = new ArrayList<>();
        Location senderLoc = sender.getLocation();
        int range = channel.range();
        long rangeSquared = (long) range * range;
        long hintRangeSquared = (long) (range * HINT_RANGE_MULTIPLIER) * (range * HINT_RANGE_MULTIPLIER);

        for (Audience audience : new ArrayList<>(event.viewers())) {
            if (!(audience instanceof Player viewer) || viewer.equals(sender)) continue;

            if (!channelManager.canSee(viewer, channel)) {
                event.viewers().remove(viewer);
                continue;
            }
            if (channel.isUnlimited()) continue;

            if (!sameWorld(senderLoc, viewer)) {
                event.viewers().remove(viewer);
                continue;
            }
            double distanceSquared = senderLoc.distanceSquared(viewer.getLocation());
            if (distanceSquared > rangeSquared) {
                event.viewers().remove(viewer);
                if (channel.hasActionBarHint() && distanceSquared <= hintRangeSquared) {
                    hintTargets.add(viewer);
                }
            }
        }
        return hintTargets;
    }

    // same world
    private boolean sameWorld(Location a, Player b) {
        return a.getWorld() != null && b.getWorld() != null
                && a.getWorld().equals(b.getWorld());
    }

    // remaining recipients of the event
    private Set<UUID> recipients(AsyncChatEvent event) {
        Set<UUID> result = new HashSet<>();
        for (Audience audience : event.viewers()) {
            if (audience instanceof Player p) {
                result.add(p.getUniqueId());
            }
        }
        return result;
    }

    // apply PAPI placeholders, logging failures
    private String applyPlaceholders(String text, Player source) {
        try {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(source, text);
            }
        } catch (Throwable placeholderFailure) {
            plugin.getLogger().warning(
                    "[ChatListener] PlaceholderAPI substitution failed"
                            + " for player '" + source.getName() + "': "
                            + placeholderFailure.getMessage());
        }
        return text;
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
