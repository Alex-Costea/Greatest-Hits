package GreatestHits;

import java.util.ArrayList;
import static GreatestHits.ChartSimulator.ran;

// Artist class: contains the artist info
class Artist {
    private String name;
    private final ArrayList<Song> songsReleased = new ArrayList<>();
    //how popular the artist is. song popularity depends on this
    private double popularity;
    //how good their music is. song quality depends on this
    private double quality;
    private int ID;
    Artist(String name,int ID)
    {
        this.setName(name);
        this.setID(ID);
        this.setPopularity(ran.nextInt(0,911)/1000.0);
        this.setQuality(ran.nextInt(100,1001)/100.0);
    }


    //How likely is it that this artist will release a song this week?
    double songReleaseProb()
    {
        return ran.nextInt(101)/100.0;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public ArrayList<Song> getSongsReleased() {
        return songsReleased;
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
}