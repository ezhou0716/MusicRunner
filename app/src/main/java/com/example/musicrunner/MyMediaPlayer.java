package com.example.musicrunner;

import android.media.AudioAttributes;
import android.media.MediaPlayer;

/**
 * Singleton instance of MediaPlayer.
 */
public class MyMediaPlayer {
    /**
     * Static instance.
     */
    static MediaPlayer instance;

    /**
     * Return the static instance of MediaPlayer.
     * @return
     */
    public static MediaPlayer getInstance(){
        if(instance == null){
            instance = new MediaPlayer();
            instance.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
        }
        return instance;
    }

    /**
     * Current index value of the song
     */
    public static int currentIndex = -1;
}