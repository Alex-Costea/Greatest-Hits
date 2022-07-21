package GreatestHits;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static GreatestHits.ChartSimulator.ran;

class Song {
    public String name;
    private final double decay; //how quickly the song fan reception goes down
    private double popularity;
    private double fanReception; // goes down in time
    private static int totalID=0; // which ID are we at?
    public int ID; // current ID
    public int week; //weeks since release
    public double points; // correlated to popularity * reception
    public Artist artist; // artist of the song
    private double fullPoints=0;

    Song(String name,Artist artist)
    {
        this.name=name;
        this.artist=artist;
        this.decay=ran.nextInt(90,97)*0.01; //between 0.90 and 0.96
        double offsetPopularity=ran.nextGaussian()/5; //random value used to calculate popularity
        this.popularity=Math.pow(min(0.91,max(0,artist.popularity+offsetPopularity)),4);
        double offsetQuality=ran.nextGaussian(); //random value used to calculate quality
        this.fanReception= min(10, max(1, artist.quality + offsetQuality));
        totalID+=1;
        this.ID =totalID;
        this.week=0;
    }

    //add one week of release to song
    public void addWeek()
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

    public void addFullPoints() {
        fullPoints+=points;
    }

    public double getFullPoints()
    {
        return fullPoints;
    }
}
