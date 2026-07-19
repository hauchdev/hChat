package dev.hauch.hchat.v1_19_R1;

import dev.hauch.hchat.api.platform.HChatBossBar;
import dev.hauch.hchat.api.platform.PlatformAdapter;
import dev.hauch.hchat.api.platform.SoundData;
import dev.hauch.hchat.api.platform.SoundLookup;
import dev.hauch.hchat.api.platform.TitleData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungee.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

// class PlatformAdapter_v1_19_R1
public final class PlatformAdapter_v1_19_R1 implements PlatformAdapter {

    private static final BungeeComponentSerializer BUNGEE =
            BungeeComponentSerializer.get();
    private static final LegacyComponentSerializer LEGACY_AMP =
            LegacyComponentSerializer.legacyAmpersand();

    @Override
    // get id
    public String getId() {
        return "v1_19_R1";
    }

    @Override
    // get display name
    public String getDisplayName() {
        return "1.19 / 1.19.1 / 1.19.2";
    }

    @Override
    // send action bar
    public void sendActionBar(Player player, Component message) {
        player.spigot().sendMessage(
                net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                BUNGEE.serialize(message));
    }

    @Override
    // clear action bar
    public void resetActionBar(Player player) {
        sendActionBar(player, Component.empty());
    }

    @Override
    // send title
    public void sendTitle(Player player, TitleData data) {
        String title = LEGACY_AMP.serialize(data.title());
        String subtitle = LEGACY_AMP.serialize(data.subtitle());
        player.sendTitle(title, subtitle,
                data.fadeIn(), data.stay(), data.fadeOut());
    }

    @Override
    // clear title
    public void resetTitle(Player player) {
        player.resetTitle();
    }

    @Override
    // make boss bar
    public HChatBossBar createBossBar(Component title, BarColor color, BarStyle style) {
        BossBar raw = Bukkit.createBossBar(
                BUNGEE.serialize(title), color, style);
        return new BukkitHChatBossBar(raw);
    }

    @Override
    // play sound
    public void playSound(Player player, SoundData sound) {
        try {
            Sound resolved = SoundLookup.resolve(sound.key());
            player.playSound(player.getLocation(), resolved,
                    sound.volume(), sound.pitch());
        } catch (IllegalArgumentException ignored) {
            
        }
    }

    @Override
    // stop sound
    public void stopSound(Player player, Sound sound) {
        player.stopSound(sound);
    }

    @Override
    // stop all sounds
    public void stopAllSounds(Player player) {
        player.stopAllSounds();
    }

    @Override
    // send message
    public void sendMessage(CommandSender sender, Component message) {
        sender.sendMessage(BUNGEE.serialize(message));
    }

    @Override
    // get server motd
    public String getServerMotd() {
        return Bukkit.getServer().getMotd();
    }

    @Override
    // get online players
    public int getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().size();
    }

    @Override
    // get max players
    public int getMaxPlayers() {
        return Bukkit.getMaxPlayers();
    }

    // class BukkitHChatBossBar
    private static final class BukkitHChatBossBar implements HChatBossBar {

        private final BossBar delegate;
        private final Set<Player> viewers = new HashSet<>();

        BukkitHChatBossBar(BossBar delegate) {
            this.delegate = delegate;
        }

        @Override
        // set title
        public void setTitle(Component title) {
            delegate.setTitle(LegacyComponentSerializer.section().serialize(title));
        }

        @Override
        // set color
        public void setColor(BarColor color) {
            delegate.setColor(color);
        }

        @Override
        // set style
        public void setStyle(BarStyle style) {
            delegate.setStyle(style);
        }

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
        // dispose data
        public void dispose() {
            delegate.removeAll();
            viewers.clear();
        }

        @Override
        // viewers data
        public Set<Player> viewers() {
            return Set.copyOf(viewers);
        }
    }
}
