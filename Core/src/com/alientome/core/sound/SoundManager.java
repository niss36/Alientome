package com.alientome.core.sound;

import java.net.URL;

public interface SoundManager {

    void load();

    void register(String mediaID, String volumePropertyKey, URL mediaURL);

    void playOnce(String mediaID);

    void playLooping(String mediaID);

    void stopPlaying(String mediaID);
}
