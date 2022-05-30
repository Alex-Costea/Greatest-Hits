import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

class Song {
    String name;
    static Random ran = new Random();
    double decay; //how quickly the song fan reception goes down
    double popularity;
    double quality; // how popular the song is at first
    double fanReception; // goes down in time
    static int totalID=0; // which ID are we at?
    int ID; // current ID
    int week; //weeks since release
    double points; // correlated to popularity * reception
    int artistId;
    Artist artist; // artist of the song

    Song(String name,Artist artist)
    {
        this.name=name;
        this.artistId=artist.ID;
        this.artist=artist;
        this.decay=ran.nextInt(90,97)*0.01; //between 0.90 and 0.96
        double offsetPopularity=ran.nextGaussian()/5; //random value used to calculate popularity
        this.popularity=Math.pow(min(0.91,max(0,artist.popularity+offsetPopularity)),4);
        double offsetQuality=ran.nextGaussian(); //random value used to calculate quality
        this.quality=min(10,max(1,artist.quality+offsetQuality));
        this.fanReception=this.quality;
        totalID+=1;
        this.ID =totalID;
        this.week=0;
    }

    //add one week of release to song
    void addWeek()
    {
        this.week++;
        //I have no idea how I came up with this
        double hype=Math.pow(10,ran.nextInt(40,161)/100.0)
                /Math.pow(10,1.6)/2.5*Math.pow(0.99,this.week-1);

        this.popularity=1-(1-this.popularity)*(1-hype);
        this.fanReception=this.fanReception*
                (this.decay+ran.nextInt(21)*0.001);

        //again, no clue
        this.points=Math.pow(this.popularity*this.fanReception/10,1.5)*100;
        this.points=Math.pow(this.points,1.5)/((100/this.points))*2;
    }

}
