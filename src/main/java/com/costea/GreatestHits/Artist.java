package com.costea.GreatestHits;

import java.util.ArrayList;
import static com.costea.GreatestHits.ChartSimulator.ran;
import static java.lang.Math.*;

// Artist class: contains the artist info
class Artist {
    private String name;
    //how popular the artist is. song popularity depends on this
    private double popularity;
    //how good their music is. song quality depends on this
    private double quality;
    private int ID;
    private final double releaseOffset= ran.nextDouble(Math.PI*2);
    private final double releaseSchedule=ran.nextDouble(5.0,10.0);

    Artist(String name,int ID)
    {
        this.setName(name);
        this.setID(ID);
        this.setPopularity(ran.nextInt(0,911)/1000.0);
        this.setQuality(ran.nextInt(100,1001)/100.0);
    }


    //How likely is it that this artist will release a song this week?
    //Assumed to be evenly distributed from 0 to 1, although it is not strictly speaking
    double songReleaseProb(int week)
    {
        //TODO: Improve formula
        double weekLikelihood = sin(week / releaseSchedule + releaseOffset)/2.57;
        return min(1,max(0,ran.nextGaussian(0.5,0.1)+weekLikelihood));
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public double getPopularity() {
        return popularity;
    }

    private void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public double getQuality() {
        return quality;
    }

    private void setQuality(double quality) {
        this.quality = quality;
    }

    public int getID() {
        return ID;
    }

    private void setID(int ID) {
        this.ID = ID;
    }

    public double getReleaseOffset() {
        return releaseOffset;
    }

    public double getReleaseSchedule() {
        return releaseSchedule;
    }
}