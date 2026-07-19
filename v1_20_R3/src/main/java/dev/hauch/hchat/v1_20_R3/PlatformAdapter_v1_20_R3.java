package dev.hauch.hchat.v1_20_R3;

import dev.hauch.hchat.api.platform.HChatBossBar;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import dev.hauch.hchat.api.platform.PlatformAdapter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import dev.hauch.hchat.api.platform.SoundData;
import dev.hauch.hchat.api.platform.SoundLookup;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import dev.hauch.hchat.api.platform.TitleData;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Sound;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.boss.BarColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.boss.BarStyle;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.boss.BossBar;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.time.Duration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import java.util.HashSet;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import java.util.Set;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

// class PlatformAdapter_v1_20_R3
public final class PlatformAdapter_v1_20_R3 implements PlatformAdapter {

    @Override
    // get id
    public String getId() { return "v1_20_R3"; }

    @Override
    // get display name
    public String getDisplayName() { return "1.20.3 / 1.20.4"; }

    @Override
    // send action bar
    public void sendActionBar(Player player, Component message) {
        player.sendActionBar(message);
    }

    @Override
    // clear action bar
    public void resetActionBar(Player player) {
        player.sendActionBar(Component.empty());
    }

    @Override
    // send title
    public void sendTitle(Player player, TitleData data) {
        String title = net.kyori.adventure.text.serializer.legacy
                .LegacyComponentSerializer.legacyAmpersand()
                .serialize(data.title());
        String subtitle = net.kyori.adventure.text.serializer.legacy
                .LegacyComponentSerializer.legacyAmpersand()
                .serialize(data.subtitle());
        player.sendTitle(title, subtitle,
                data.fadeIn(), data.stay(), data.fadeOut());
    }

    @Override
    // clear title
    public void resetTitle(Player player) { player.resetTitle(); }

    @Override
    // make boss bar
    public HChatBossBar createBossBar(Component title, BarColor color, BarStyle style) {
        BossBar raw = Bukkit.createBossBar(LegacyComponentSerializer.section().serialize(title), color, style);
        return new BukkitHChatBossBar(raw);
    }

    @Override
    // play sound
    public void playSound(Player player, SoundData sound) {
        try {
            Sound resolved = SoundLookup.resolve(sound.key());
            player.playSound(player.getLocation(), resolved,
                    sound.volume(), sound.pitch());
        } catch (IllegalArgumentException ignored) { }
    }

    @Override
    // stop sound
    public void stopSound(Player player, Sound sound) { player.stopSound(sound); }

    @Override
    // stop all sounds
    public void stopAllSounds(Player player) { player.stopAllSounds(); }

    @Override
    // send message
    public void sendMessage(CommandSender sender, Component message) {
        sender.sendMessage(message);
    }

    @Override
    // get server motd
    public String getServerMotd() { return Bukkit.getServer().getMotd(); }

    @Override
    // get online players
    public int getOnlinePlayers() { return Bukkit.getOnlinePlayers().size(); }

    @Override
    // get max players
    public int getMaxPlayers() { return Bukkit.getMaxPlayers(); }

    // class BukkitHChatBossBar
    private static final class BukkitHChatBossBar implements HChatBossBar {

        private final BossBar delegate;
        private final Set<Player> viewers = new HashSet<>();

        BukkitHChatBossBar(BossBar delegate) { this.delegate = delegate; }

        @Override
        // set title
        public void setTitle(Component title) { delegate.setTitle(LegacyComponentSerializer.section().serialize(title)); }

        @Override
        // set color
        public void setColor(BarColor color) { delegate.setColor(color); }

        @Override
        // set style
        public void setStyle(BarStyle style) { delegate.setStyle(style); }

        @Override
        // set progress
        public void setProgress(double progress) {
            delegate.setProgress(Math.max(0.0, Math.min(1.0, progress)));
        }

        @Override
        // show to
        public void showTo(Player... players) {
            for (Player p : players) {
                delegate.addPlayer(p);
                viewers.add(p);
            }
        }

        @Override
        // hide from
        public void hideFrom(Player... players) {
            for (Player p : players) {
                delegate.removePlayer(p);
                viewers.remove(p);
            }
        }

        @Override
        // dispose
        // dispose data
        public void dispose() { delegate.removeAll(); viewers.clear(); }

        @Override
        // viewers
        // viewers viewers
        // viewers data
        public Set<Player> viewers() { return Set.copyOf(viewers); }
    }
}
