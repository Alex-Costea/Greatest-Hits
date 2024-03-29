package com.costea.GreatestHits.DataObjects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

import static com.costea.GreatestHits.Statics.Statics.ran;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static com.costea.GreatestHits.Statics.Statics.nextSongID;

public class Song {
    private static final int MAGIC_VALUE=999;
    private String name;
    private final double decay; //how quickly the song fan reception goes down
    private double popularity;
    private double fanReception; // goes down in time

    private int ID; // current ID
    private int week; //weeks since release
    private double points; // correlated to popularity * reception
    private Artist artist; // artist of the song
    private Integer peak = MAGIC_VALUE;
    private Integer currentPosition = MAGIC_VALUE;
    private int artistID;

    public Song(String name, Artist artist)
    {
        this.setName(name);
        this.artist=artist;
        this.decay=ran.nextInt(90,97)*0.01; //between 0.90 and 0.96
        double offsetPopularity=ran.nextGaussian()/5; //random value used to calculate popularity
        this.popularity=Math.pow(min(0.91,max(0, artist.getPopularity() +offsetPopularity)),4);
        double offsetQuality=ran.nextGaussian(); //random value used to calculate quality
        this.fanReception= min(10, max(1, artist.getQuality() + offsetQuality));
        nextSongID +=1;
        this.setID(nextSongID);
        this.setWeek(0);
    }

    public void transmitArtistData(ArrayList<Artist> listOfArtists)
    {
        if(artist==null)
            for(Artist artist : listOfArtists)
                if(artist.getID()==artistID)
                    this.artist=artist;
    }

    Song(){
        decay=1;
    }

    public Integer getPeak() {
        return peak;
    }

    public void setPeak(Integer peak) {
        this.peak = peak;
    }

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Integer currentPosition) {
        this.currentPosition = currentPosition;
    }

    //add one week of release to song
    public void addWeek()
    {
        this.setWeek(this.getWeek() + 1);
        //Complex math formula for setting hype
        double hype=Math.pow(10,ran.nextInt(40,161)/100.0)
                /Math.pow(10,1.6)/2.5*Math.pow(0.99, this.getWeek() -1);
        //Higher hype leads to a bigger increase of popularity
        this.popularity=1-(1-this.popularity)*(1-hype);
        //Reception decreases as people get sick of the song
        this.fanReception=this.fanReception*
                (this.decay+ran.nextInt(21)*0.001);

        //formula for setting points based on popularity and fan reception
        this.setPoints(Math.pow(this.popularity*this.fanReception/10,1.5)*100);
        this.setPoints(Math.pow(this.getPoints(),1.5)/((100/ this.getPoints()))*2);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    @JsonIgnore
    public String getAristName()
    {
        return artist.getName();
    }

    public int getArtistID()
    {
        return artist.getID();
    }

    public void setArtistID(int ID)
    {
        artistID=ID;
    }

    public double getDecay() {
        return decay;
    }

    public double getPopularity() {
        return popularity;
    }

    public double getFanReception() {
        return fanReception;
    }
}
