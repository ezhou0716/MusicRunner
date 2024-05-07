package com.example.musicrunner;

public class Song {
    public String path;
    public int duration;
    public int bpm;
    public String title;
    public String artist;
    public Song(String path, int duration, int bpm, String title, String artist){
        this.path = path;
        this.duration = duration;
        this.bpm = bpm;
        this.title = title;
        this.artist = artist;
    }

    public Song(String field, int duration) {
    }

    public String getPath(){
        return path;
    }
    public int getDuration(){
        return duration;
    }
    public int getBpm(){
        return bpm;
    }
    public String getTitle(){
        return title;
    }
    public String getArtist(){
        return artist;
    }
}
