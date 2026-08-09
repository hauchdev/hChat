package dev.hauch.hchat.service;

import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.manager.AfkManager;
import dev.hauch.hchat.utils.MessageFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// class DynamicPlaceholderResolver
public final class DynamicPlaceholderResolver {

    private static final String[] ROMAN_NUMERALS = {"I", "II", "III", "IV", "V",
            "VI", "VII", "VIII", "IX", "X"};

    private final PluginConfig config;
    private final AfkManager afkManager;

    // make DynamicPlaceholderResolver
    public DynamicPlaceholderResolver(PluginConfig config, AfkManager afkManager) {
        this.config = config;
        this.afkManager = afkManager;
    }

    // resolve all dynamic tokens inside a chat format. Tokens that do not
    // appear in the format are skipped, so [item] / [afk] are not built
    // for every chat message.
    public Component resolve(Component format, Player player) {
        String plain = PlainTextComponentSerializer.plainText().serialize(format);
        Component result = format;
        if (plain.contains("[ping]")) result = replace(result, "[ping]", resolvePing(player));
        if (plain.contains("[item]")) result = replace(result, "[item]", resolveItem(player));
        if (plain.contains("[coords]")) result = replace(result, "[coords]", resolveCoords(player));
        if (plain.contains("[world]")) result = replace(result, "[world]", resolveWorld(player));
        if (plain.contains("[afk]")) result = replace(result, "[afk]", resolveAfk(player));
        return result;
    }

    // resolve [ping]: colored symbol plus optional latency value
    public Component resolvePing(Player player) {
        if (!config.isPlaceholderEnabled("ping")) return Component.empty();
        int ping = Math.max(0, player.getPing());
        String color;
        if (ping < config.getPingGoodMax()) {
            color = config.getPingGoodColor();
        } else if (ping < config.getPingMediumMax()) {
            color = config.getPingMediumColor();
        } else {
            color = config.getPingBadColor();
        }
        Component result = MessageFormatter.format(color + config.getPingSymbol());
        if (config.isPingValueShown()) {
            result = result.append(Component.space()).append(MessageFormatter.format(
                    config.getPingValueColor() + ping + config.getPingValueSuffix()));
        }
        return result;
    }

    // resolve [item]: main-hand item with a NoNChat-style tooltip on hover
    // (name, enchantments, durability bar and lore), built from APIs that
    // exist on every supported Minecraft version
    public Component resolveItem(Player player) {
        if (!config.isPlaceholderEnabled("item")) return Component.empty();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) return Component.empty();
        try {
            ItemMeta meta = item.getItemMeta();

            // display name: custom name (with colors) or localized item name
            String customName = (meta != null && meta.hasDisplayName())
                    ? meta.getDisplayName() : null;
            Component display = (customName != null && !customName.isBlank())
                    ? MessageFormatter.format(customName)
                    : Component.translatable(translationKey(item));

            List<Component> lines = new ArrayList<>();
            lines.add(display);

            if (meta != null) {
                // enchantments, e.g. "Sharpness V"
                if (meta.hasEnchants()) {
                    for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                        lines.add(MessageFormatter.format("&7")
                                .append(Component.translatable(
                                        enchantTranslationKey(entry.getKey())))
                                .append(Component.text(" " + romanNumeral(entry.getValue()))));
                    }
                }
                // lore
                if (meta.hasLore()) {
                    for (String lore : meta.getLore()) {
                        lines.add(MessageFormatter.format(lore));
                    }
                }
            }

            // durability bar
            int maxDurability = item.getType().getMaxDurability();
            if (maxDurability > 0) {
                lines.add(durabilityBar(maxDurability - item.getDurability(), maxDurability));
            }

            Component hover = Component.empty();
            for (int i = 0; i < lines.size(); i++) {
                if (i > 0) hover = hover.append(Component.newline());
                hover = hover.append(lines.get(i));
            }
            return display.hoverEvent(HoverEvent.showText(hover));
        } catch (Throwable t) {
            return MessageFormatter.format("&f" + item.getType().name()
                    .toLowerCase(Locale.ROOT).replace('_', ' '));
        }
    }

    // item / block.minecraft.<id> translation key used as display name
    private String translationKey(ItemStack item) {
        String prefix = item.getType().isBlock() ? "block" : "item";
        return prefix + ".minecraft." + item.getType().name().toLowerCase(Locale.ROOT);
    }

    // enchantment.minecraft.<id> translation key
    private String enchantTranslationKey(Enchantment enchantment) {
        return "enchantment.minecraft."
                + enchantment.getName().toLowerCase(Locale.ROOT);
    }

    // durability bar: ten colored blocks plus remaining/max text
    private Component durabilityBar(int remaining, int max) {
        int safe = Math.min(max, Math.max(0, remaining));
        double ratio = (double) safe / max;
        String color = ratio > 0.5 ? "&a" : ratio > 0.25 ? "&e" : "&c";
        int filled = Math.max(0, Math.min(10, (int) Math.round(ratio * 10)));
        StringBuilder bar = new StringBuilder(color);
        for (int i = 0; i < 10; i++) {
            bar.append(i < filled ? "█" : "&8█");
        }
        bar.append(" &7").append(safe).append('/').append(max);
        return MessageFormatter.format(bar.toString());
    }

    // roman numerals for enchant levels 1-10
    private String romanNumeral(int level) {
        if (level >= 1 && level <= 10) {
            return ROMAN_NUMERALS[level - 1];
        }
        return String.valueOf(level);
    }

    // resolve [coords]: player position as x, y, z
    public Component resolveCoords(Player player) {
        if (!config.isPlaceholderEnabled("coords")) return Component.empty();
        String text = config.getCoordsFormat()
                .replace("{x}", String.valueOf(player.getLocation().getBlockX()))
                .replace("{y}", String.valueOf(player.getLocation().getBlockY()))
                .replace("{z}", String.valueOf(player.getLocation().getBlockZ()));
        return MessageFormatter.format(text);
    }

    // resolve [world]: current world name
    public Component resolveWorld(Player player) {
        if (!config.isPlaceholderEnabled("world")) return Component.empty();
        String world = player.getWorld().getName();
        return MessageFormatter.format(config.getWorldFormat().replace("{world}", world));
    }

    // resolve [afk]: "[AFK]" prefix when the player is away. Uses the
    // built-in AfkManager (manual /afk + inactivity detection); falls back
    // to the configured PAPI placeholder when the built-in feature is off.
    public Component resolveAfk(Player player) {
        if (!config.isPlaceholderEnabled("afk")) return Component.empty();
        boolean afk;
        if (afkManager != null && afkManager.isEnabled()) {
            afk = afkManager.isAfk(player);
        } else {
            afk = isAfkViaPlaceholder(player);
        }
        if (!afk) return Component.empty();
        return MessageFormatter.format(config.getAfkFormat());
    }

    // is the player afk (delegates to the configured PAPI placeholder)
    private boolean isAfkViaPlaceholder(Player player) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return false;
        }
        try {
            String result = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(
                    player, config.getAfkPlaceholder());
            if (result == null) return false;
            String lower = result.toLowerCase(Locale.ROOT).trim();
            for (String value : config.getAfkValues()) {
                if (lower.equals(value.trim().toLowerCase(Locale.ROOT))) {
                    return true;
                }
            }
        } catch (Throwable ignored) { }
        return false;
    }

    // replace one token with a component
    private Component replace(Component input, String token, Component replacement) {
        return input.replaceText(b -> b.matchLiteral(token).replacement(replacement));
    }
}
