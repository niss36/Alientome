package com.alientome.impl;

import com.alientome.core.SharedInstances;
import com.alientome.core.settings.Config;
import com.alientome.core.sound.SoundManager;
import com.alientome.core.util.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.Property;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.alientome.core.SharedNames.CONFIG;

public class DefaultSoundManager implements SoundManager {

    private static final Logger log = Logger.get();

    private final Map<String, MediaPlayer> players = new HashMap<>();

    @Override
    public void load() {

        register("music.main", "volume", ClassLoader.getSystemResource("Audio/Music/main.mp3"));
    }

    @Override
    public void register(String mediaID, String volumePropertyKey, URL mediaURL) {

        Media media = new Media(mediaURL.toExternalForm());
        MediaPlayer player = new MediaPlayer(media);

        player.volumeProperty().bind(createVolumeBinding(volumePropertyKey));

        players.put(mediaID, player);
    }

    @Override
    public void playOnce(String mediaID) {
        MediaPlayer player = players.get(mediaID);
        if (player != null) {
            player.setCycleCount(1);
            player.play();
        } else log.w("Unknown sound id used : " + mediaID);
    }

    @Override
    public void playLooping(String mediaID) {
        MediaPlayer player = players.get(mediaID);
        if (player != null) {
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.play();
        } else log.w("Unknown sound id used : " + mediaID);
    }

    @Override
    public void stopPlaying(String mediaID) {
        MediaPlayer player = players.get(mediaID);
        if (player != null)
            player.stop();
        else log.w("Unknown sound id used : " + mediaID);
    }

    private DoubleBinding createVolumeBinding(String volumePropertyKey) {

        Config config = SharedInstances.get(CONFIG);

        Property<Integer> volumeProperty = config.getProperty(volumePropertyKey);
        return Bindings.createDoubleBinding(() -> volumeProperty.getValue() / 100.0, volumeProperty);
    }
}
