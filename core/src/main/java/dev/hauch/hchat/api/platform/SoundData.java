package dev.hauch.hchat.api.platform;

// record SoundData
// sound data
public record SoundData(String key, float volume, float pitch) {

    public SoundData {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Sound key cannot be null or blank");
        }
        if (volume < 0.0f) {
            throw new IllegalArgumentException("Volume cannot be negative: " + volume);
        }
        if (pitch < 0.0f) {
            throw new IllegalArgumentException("Pitch cannot be negative: " + pitch);
        }
    }

    // of data
    public static SoundData of(String key, float volume, float pitch) {
        return new SoundData(key.toUpperCase(), volume, pitch);
    }

    // with volume
    public SoundData withVolume(float newVolume) {
        return new SoundData(key, newVolume, pitch);
    }

    // with pitch
    public SoundData withPitch(float newPitch) {
        return new SoundData(key, volume, newPitch);
    }
}
