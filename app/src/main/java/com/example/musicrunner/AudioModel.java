package com.example.musicrunner;

import java.io.Serializable;

/**
 * This class captured information about a piece of music
 */
public class AudioModel implements Serializable {
    /**
     *  The location / URL of the music
     */
    String path;

    /**
     * The title of the music
     */
    String title;
    /**
     * Duration of the music. However, this may be caculated at runtime.
     */
    int duration;

    /**
     * The lower end of the pace this music fits.
     */
    int paceLow;

    /**
     * The higher end of the pace this music fits.
     */
    int paceHigh;


    /**
     * Constructor method with pace
     * @param path
     * @param title
     * @param duration
     * @param paceLow
     * @param paceHigh
     */
    public AudioModel(String path, String title, int duration, int paceLow, int paceHigh) {
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.paceLow = paceLow;
        this.paceHigh = paceHigh;
    }

    /**
     * Constructor method without pace
     * @param path
     * @param title
     * @param duration
     */
    public AudioModel(String path, String title, int duration) {
        this.path = path;
        this.title = title;
        this.duration = duration;
    }

    /**
     * Returns the path or URL of the music
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns title of the music
     * @return
     */
    public String getTitle() {
        return title;
    }


    /**
     * Returns the duration of the music.
     * @return
     */
    public int getDuration() {
        return duration;
    }


    /**
     * Returns the lower end of pace for this music.
     * @return
     */
    public int getPaceLow() {
        return paceLow;
    }

    /**
     * Returns the higher end of the pace for this music.
     *
     * @return
     */
    public int getPaceHigh() {
        return paceHigh;
    }


    /**
     * Returns true if the given pace is within the pace range.
     * @param pace
     * @return
     */
    public boolean inPaceRange(float pace) {
        return (pace >= paceLow && pace < paceHigh);
    }
}