package dev.hauch.hchat.listener;

import dev.hauch.hchat.api.platform.SoundLookup;
import dev.hauch.hchat.api.premium.PremiumChatEnhancer;
import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.manager.ChannelManager;
import dev.hauch.hchat.manager.ChatLockManager;
import dev.hauch.hchat.manager.ChatReplay;
import dev.hauch.hchat.manager.MentionManager;
import dev.hauch.hchat.manager.SlowmodeManager;
import dev.hauch.hchat.model.ChatChannel;
import dev.hauch.hchat.service.DynamicPlaceholderResolver;
import dev.hauch.hchat.utils.FilterAction;
import dev.hauch.hchat.utils.MessageFormatter;
import dev.hauch.hchat.utils.filter.FilterChain;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
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
    private final FilterChain filterChain;
    private final ChannelManager channelManager;
    private final SlowmodeManager slowmodeManager;
    private final ChatLockManager chatLockManager;
    private final MentionManager mentionManager;
    private final DynamicPlaceholderResolver placeholderResolver;
    private final ChatReplay chatReplay;
    private final List<PremiumChatEnhancer> enhancers;

    // make ChatListener
    public ChatListener(Plugin plugin,
                        PluginConfig config,
                        PluginMessages messages,
                        FilterChain filterChain,
                        ChannelManager channelManager,
                        SlowmodeManager slowmodeManager,
                        ChatLockManager chatLockManager,
                        MentionManager mentionManager,
                        DynamicPlaceholderResolver placeholderResolver,
                        ChatReplay chatReplay,
                        List<PremiumChatEnhancer> enhancers) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.filterChain = filterChain;
        this.channelManager = channelManager;
        this.slowmodeManager = slowmodeManager;
        this.chatLockManager = chatLockManager;
        this.mentionManager = mentionManager;
        this.placeholderResolver = placeholderResolver;
        this.chatReplay = chatReplay;
        this.enhancers = enhancers == null ? List.of() : List.copyOf(enhancers);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    // on chat
    public void onChat(AsyncChatEvent event) {
        // An event already cancelled by an earlier listener (e.g. the
        // premium staff chat at NORMAL priority) must not trigger side
        // effects here: mentions, cooldowns or filter warnings.
        if (event.isCancelled()) {
            return;
        }

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

        // 2.5 premium enhancers: block and/or transform the message.
        //    The free edition has an empty list, so this is a no-op.
        for (PremiumChatEnhancer enhancer : enhancers) {
            if (!enhancer.allowChat(sender, plainMessage)) {
                event.setCancelled(true);
                return;
            }
        }
        String transformed = plainMessage;
        for (PremiumChatEnhancer enhancer : enhancers) {
            transformed = enhancer.transformMessage(sender, transformed);
        }
        if (!transformed.equals(plainMessage)) {
            plainMessage = transformed;
            // Keep legacy color codes as literal text: the {message}
            // replacement further down re-renders the text through
            // MessageFormatter, so codes survive the round trip.
            event.message(Component.text(transformed));
        }

        // 3. filter chain: word filter, anti-caps, anti-unicode, anti-ad, anti-spam
        Optional<FilterChain.Result> filterResult = filterChain.filter(sender, plainMessage);
        if (filterResult.isPresent()) {
            FilterChain.Result result = filterResult.get();
            if (result.blocked()) {
                sender.sendMessage(MessageFormatter.format(messages.getString("message-filtered")));
                event.setCancelled(true);
                return;
            }
            event.message(MessageFormatter.format(result.text()));
            plainMessage = result.text();
            if (result.warned()) {
                notifyStaff(sender, plainMessage);
            }
        }

        // 4. speak permission
        if (!channelManager.canSpeak(sender, channel)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("channel-no-access"),
                    Map.of("channel", channel.id())));
            event.setCancelled(true);
            return;
        }

        // 4.5 chat lock
        if (chatLockManager.isLocked() && !chatLockManager.canBypass(sender)) {
            sender.sendMessage(MessageFormatter.format(messages.getString("chatlock-chat-locked")));
            event.setCancelled(true);
            return;
        }

        // 4.6 global slowmode: one message per player every N seconds
        long slowmodeRemaining = slowmodeManager.cooldownRemaining(sender);
        if (slowmodeRemaining > 0) {
            sender.sendMessage(MessageFormatter.format(messages.getString("slowmode-chat-blocked"),
                    Map.of("seconds", String.format(Locale.ROOT, "%.1f",
                            slowmodeRemaining / 1000.0))));
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

        // 5.5 mass-mention gate: validate permission + cooldown. Placed after
        //     every block check and before any cooldown is recorded, so a
        //     message cancelled here consumes nothing.
        if (!checkMassMentionGate(sender, plainMessage, event)) {
            return;
        }

        // 6. record cooldowns now that the message is definitely going through
        slowmodeManager.record(sender);
        channelManager.recordCooldown(sender, "channel:" + channel.id());

        // 7. filter who sees the message (see-permission + distance range)
        List<Player> hintTargets = filterViewers(event, sender, channel);

        // 8. action-bar hint for players just outside the range
        if (!hintTargets.isEmpty() && channel.hasActionBarHint()) {
            for (Player nearby : hintTargets) {
                if (channelManager.shouldShowHint(nearby)) {
                    nearby.sendActionBar(MessageFormatter.format(channel.actionBarHint()));
                }
            }
        }

        // 9. recipients - who can actually see this message
        Set<UUID> recipients = recipients(event);

        // 10. mentions - notify only players who can see the message
        Matcher matcher = MENTION_PATTERN.matcher(plainMessage);
        while (matcher.find()) {
            String mentionedName = matcher.group(1);
            Player mentioned = Bukkit.getPlayerExact(mentionedName);
            if (mentioned != null && mentioned.isOnline()
                    && !mentioned.equals(sender)
                    && recipients.contains(mentioned.getUniqueId())) {
                mentionsNotify(mentioned, sender);
            }
        }

        // 11. mass mentions: @everyone (all online) and @here (same world)
        fireMassMentions(sender, plainMessage, recipients);

        // 12. the message body as a component: mention tokens that point at
        //     a visible player get the configured color plus an optional
        //     bold highlight. Built once and embedded into the format below
        //     (the renderer never re-reads event.message(), so this is the
        //     only place the styling can be applied).
        Component messageComponent = buildMessageComponent(plainMessage, sender, recipients);

        // 13. per-channel format, clickable to switch channel. The default
        //     channel has no format in config.yml, so it falls back to the
        //     permission-based chat.formats resolution (vip/mvp/admin).
        boolean defaultChannel = channelManager.isDefault(channel);
        String format = channel.hasFormat() ? channel.format()
                : config.resolveChatFormat(sender);

        // The format only depends on the sender, so build it once instead of
        // once per viewer: PAPI, dynamic tokens ([ping] [item] [coords]
        // [world] [afk]) and the message are all resolved before the renderer.
        String resolved = format
                .replace("{prefix}", "%vault_prefix%")
                .replace("{suffix}", "%vault_suffix%")
                .replace("{player}", sender.getName())
                .replace("{world}", sender.getWorld().getName());
        resolved = applyPlaceholders(resolved, sender);

        for (PremiumChatEnhancer enhancer : enhancers) {
            resolved = enhancer.transformFormat(sender, resolved);
        }

        Component rendered = MessageFormatter.format(resolved);

        // tokens are resolved before {message} is inserted, so tokens typed
        // by players stay literal
        rendered = placeholderResolver.resolve(rendered, sender);

        // {message} is replaced with the styled message component - color
        // codes and PAPI placeholders inside the message keep working
        Component renderedFormat = rendered.replaceText(b -> b.matchLiteral("{message}")
                .replacement(messageComponent));

        event.renderer((source, sourceDisplayName, message, viewer) -> {
            Component line = renderedFormat;
            // let receivers click to join this channel (like NoNChat)
            if (!defaultChannel && viewer instanceof Player v
                    && channelManager.canSee(v, channel)) {
                line = line.hoverEvent(HoverEvent.showText(MessageFormatter.format(
                                messages.getString("channel-click-hover"),
                                Map.of("channel", channel.id()))))
                        .clickEvent(ClickEvent.runCommand("/channel " + channel.id()));
            } else if (viewer instanceof Player && config.isHoverTextEnabled()) {
                // hover tooltip + click-to-reply on the default channel
                Component hover = MessageFormatter.formatHover(
                        config.getHoverTextFormat(),
                        Map.of("player", sender.getName()));
                line = line.hoverEvent(HoverEvent.showText(hover))
                        .clickEvent(ClickEvent.suggestCommand("/msg " + sender.getName()));
            }
            return line;
        });

        // 14. record the line for /hchat replay. Channels with a
        //     see-permission (staff, vip...) are never buffered so a plain
        //     player cannot read them back via /hchat replay.
        if (chatReplay != null && !channel.hasSeePermission()) {
            chatReplay.record("[" + channel.id() + "] "
                    + sender.getName() + ": " + plainMessage);
        }
    }

    // build the chat message component: mention tokens that point at a
    // visible online player get the configured color plus an optional bold
    // highlight; every other segment is PAPI-resolved and legacy-formatted
    private Component buildMessageComponent(String message, Player sender,
                                            Set<UUID> recipients) {
        boolean colorize = config.isMentionColorsEnabled();
        Matcher matcher = MENTION_PATTERN.matcher(message);
        Component result = Component.empty();
        int lastEnd = 0;
        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                result = result.append(MessageFormatter.format(
                        applyPlaceholders(message.substring(lastEnd, matcher.start()), sender)));
            }
            String mention = matcher.group();
            String name = matcher.group(1);
            Player mentioned = Bukkit.getPlayerExact(name);
            if (colorize && mentioned != null && mentioned.isOnline()
                    && recipients.contains(mentioned.getUniqueId())) {
                Component styled = MessageFormatter.format(
                        config.getMentionColor() + mention);
                if (config.isMentionHighlightEnabled()) {
                    styled = styled.decorate(TextDecoration.BOLD);
                }
                result = result.append(styled);
            } else {
                result = result.append(MessageFormatter.format(
                        applyPlaceholders(mention, sender)));
            }
            lastEnd = matcher.end();
        }
        if (lastEnd < message.length()) {
            result = result.append(MessageFormatter.format(
                    applyPlaceholders(message.substring(lastEnd), sender)));
        }
        return result;
    }

    // validate permission and cooldown for the mass mention tokens present
    // in the message; cancels the event when they fail
    private boolean checkMassMentionGate(Player sender, String message,
                                         AsyncChatEvent event) {
        String lower = message.toLowerCase(Locale.ROOT);
        if (containsToken(lower, "@everyone")) {
            if (!sender.hasPermission(config.getEveryoneMentionPermission())) {
                sender.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
                event.setCancelled(true);
                return false;
            }
            if (!consumeMentionCooldown(event, sender, "everyone")) return false;
        }
        if (containsToken(lower, "@here")) {
            if (!sender.hasPermission(config.getHereMentionPermission())) {
                sender.sendMessage(MessageFormatter.format(messages.getString("no-permission")));
                event.setCancelled(true);
                return false;
            }
            if (!consumeMentionCooldown(event, sender, "here")) return false;
        }
        return true;
    }

    // notify every recipient for the mass mention tokens in the message
    private void fireMassMentions(Player sender, String message, Set<UUID> recipients) {
        String lower = message.toLowerCase(Locale.ROOT);
        if (containsToken(lower, "@everyone")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (recipients.contains(p.getUniqueId())) {
                    massMentionNotify(p, sender);
                }
            }
        }
        if (containsToken(lower, "@here")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (recipients.contains(p.getUniqueId())
                        && sameWorld(sender.getLocation(), p)) {
                    massMentionNotify(p, sender);
                }
            }
        }
    }

    // true when the token appears as a standalone word (not inside a longer
    // word like "@everyoneX")
    private static boolean containsToken(String lower, String token) {
        int index = lower.indexOf(token);
        while (index >= 0) {
            int end = index + token.length();
            if (end >= lower.length()
                    || !Character.isLetterOrDigit(lower.charAt(end))) {
                return true;
            }
            index = lower.indexOf(token, index + 1);
        }
        return false;
    }

    // check and consume the cooldown for a mass mention token
    private boolean consumeMentionCooldown(AsyncChatEvent event, Player sender, String key) {
        long cooldownMs = config.getEveryoneMentionCooldownSeconds() * 1000L;
        long remaining = mentionManager.cooldownRemaining(sender, key, cooldownMs);
        if (remaining > 0) {
            sender.sendMessage(MessageFormatter.format(messages.getString("mention-cooldown"),
                    Map.of("seconds", String.format(Locale.ROOT, "%.1f",
                            remaining / 1000.0))));
            event.setCancelled(true);
            return false;
        }
        mentionManager.record(sender, key);
        return true;
    }

    // notify a player about a mass mention
    private void massMentionNotify(Player target, Player sender) {
        if (target.equals(sender)) return;
        if (config.isEveryoneMentionSoundEnabled()) {
            try {
                Sound sound = SoundLookup.resolve(config.getEveryoneMentionSound());
                Bukkit.getScheduler().runTask(plugin, () -> target.playSound(
                        target.getLocation(), sound,
                        config.getEveryoneMentionSoundVolume(),
                        config.getEveryoneMentionSoundPitch()));
            } catch (IllegalArgumentException ignored) { }
        }
        Map<String, String> ph = new HashMap<>();
        ph.put("player", sender.getName());
        target.sendMessage(MessageFormatter.format(messages.getString("mentioned"), ph));
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

            if ((channel.perWorld() || !channel.isUnlimited())
                    && !sameWorld(senderLoc, viewer)) {
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
