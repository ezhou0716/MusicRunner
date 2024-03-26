package com.example.musicrunner;

import java.io.Serializable;

public class AudioModel implements Serializable {
    String path;
    String title;
    String duration;


    int paceLow;
    int paceHigh;


    public AudioModel(String path, String title, String duration, int paceLow, int paceHigh) {
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.paceLow = paceLow;
        this.paceHigh = paceHigh;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getPaceLow() {
        return paceLow;
    }

    public void setPaceLow(int paceLow) {
        this.paceLow = paceLow;
    }

    public int getPaceHigh() {
        return paceHigh;
    }

    public void setPaceHigh(int paceHigh) {
        this.paceHigh = paceHigh;
    }

    public boolean inPaceRange(float pace) {
        return (pace >= paceLow && pace < paceHigh);
    }
}