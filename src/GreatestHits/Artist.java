package GreatestHits;

import java.util.ArrayList;
import static GreatestHits.GreatestHits.ran;

// Artist class: contains the artist info
class Artist {
    String name;
    ArrayList<Song> songsReleased = new ArrayList<>();
    //how popular the artist is. song popularity depends on this
    double popularity;
    //how good their music is. song quality depends on this
    double quality;
    int ID;
    Artist(String name,int ID)
    {
        this.name=name;
        this.ID=ID;
        this.popularity=ran.nextInt(0,911)/1000.0;
        this.quality=ran.nextInt(100,1001)/100.0;
    }


    //How likely is it that this artist will release a song this week?
    double songReleaseProb()
    {
        return ran.nextInt(101)/100.0;
    }

}