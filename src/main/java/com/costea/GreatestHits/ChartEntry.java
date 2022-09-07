package com.costea.GreatestHits;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

public class ChartEntry {
    private int position;
    private String artistName;
    private String songName;
    private String insideParam;
    private int points;
    private int weeks;
    private int peak;
    private int currentWeek;

    @JsonIgnore
    public int getCurrentWeek() {
        return currentWeek;
    }

    @JsonIgnore
    public void setCurrentWeek(int currentWeek) {
        this.currentWeek = currentWeek;
    }

    public int getPeak() {
        return peak;
    }

    public void setPeak(int peak) {
        this.peak = peak;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getInsideParam() {
        return insideParam;
    }

    public void setInsideParam(String insideParam) {
        this.insideParam = insideParam;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }

    public ChartEntry() {
        this(0,"N/A","N/A","=",0,0,0);
    }

    public ChartEntry(int position, String artistName, String songName, String insideParam, int points, int weeks,int peak) {
        this.position = position;
        this.artistName = artistName;
        this.songName = songName;
        this.insideParam = insideParam;
        this.points = points;
        this.weeks = weeks;
        this.peak=peak;
    }
}
