package dev.hauch.hchat.api.platform;

import net.kyori.adventure.text.Component;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.util.Set;

// interface HChatBossBar
public interface HChatBossBar {

    void setTitle(Component title);

    void setColor(BarColor color);

    void setStyle(BarStyle style);

    void setProgress(double progress);

    void showTo(Player... players);

    void hideFrom(Player... players);

    void dispose();

    Set<Player> viewers();
}
