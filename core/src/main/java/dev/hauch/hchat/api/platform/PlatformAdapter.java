package dev.hauch.hchat.api.platform;

import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// interface PlatformAdapter
public interface PlatformAdapter {

    String getId();

    String getDisplayName();

    void sendActionBar(Player player, Component message);

    void resetActionBar(Player player);

    void sendTitle(Player player, TitleData data);

    void resetTitle(Player player);

    HChatBossBar createBossBar(Component title, BarColor color, BarStyle style);

    void playSound(Player player, SoundData sound);

    void stopSound(Player player, Sound sound);

    void stopAllSounds(Player player);

    void sendMessage(CommandSender sender, Component message);

    String getServerMotd();

    int getOnlinePlayers();

    int getMaxPlayers();
}
