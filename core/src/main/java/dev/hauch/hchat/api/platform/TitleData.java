package dev.hauch.hchat.api.platform;

import net.kyori.adventure.text.Component;

// record TitleData
// title data
public record TitleData(Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {

    public TitleData {
        if (fadeIn < 0) throw new IllegalArgumentException("fadeIn cannot be negative");
        if (stay < 0) throw new IllegalArgumentException("stay cannot be negative");
        if (fadeOut < 0) throw new IllegalArgumentException("fadeOut cannot be negative");
    }

    // of data
    public static TitleData of(Component title) {
        return new TitleData(title, Component.empty(), 10, 60, 10);
    }

    // of data
    public static TitleData of(Component title, Component subtitle) {
        return new TitleData(title, subtitle, 10, 60, 10);
    }
}
