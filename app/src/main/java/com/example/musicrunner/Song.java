package com.example.musicrunner;

/**
 * This class captured information about a song
 */
public class Song {
    /**
     * Path or URL of this song.
     */
    public String path;

    /**
     * Duration of the song.
     */
    public int duration;

    /**
     * BPM of the song.
     */
    public int bpm;

    /**
     * Title of the song.
     */
    public String title;

    /**
     * Artist of the song.
     */
    public String artist;

    /**
     * Constructor
     * @param path
     * @param duration
     * @param bpm
     * @param title
     * @param artist
     */
    public Song(String path, int duration, int bpm, String title, String artist){
        this.path = path;
        this.duration = duration;
        this.bpm = bpm;
        this.title = title;
        this.artist = artist;
    }

    /**
     * Returns path or URL of the song.
     * @return
     */
    public String getPath(){
        return path;
    }

    /**
     * Returns duration of the song.
     * @return
     */
    public int getDuration(){
        return duration;
    }

    /**
     * Returns Bpm of the song.
     * @return
     */
    public int getBpm(){
        return bpm;
    }

    /**
     * Returns the title of song.
     * @return
     */
    public String getTitle(){
        return title;
    }

    /**
     * Returns the artist of the song.
     * @return
     */
    public String getArtist(){
        return artist;
    }
}
