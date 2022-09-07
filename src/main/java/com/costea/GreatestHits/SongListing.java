package com.costea.GreatestHits;

public class SongListing {
    private String position;
    private String name;
    private String points;
    private String peak;
    private String weeks;
    private int positionAsInt;

    public SongListing()
    {}

    public SongListing(Song song)
    {
        Integer position=song.getCurrentPosition();
        this.position=position>20?"N/A":Integer.toString(position);

        this.positionAsInt=position;

        this.name=song.getName();

        int points=(int)song.getPoints();
        this.points=position>20?"N/A": Integer.toString(points);

        int peak = song.getPeak();
        this.peak=peak>20?"N/A": Integer.toString(peak);

        this.weeks=Integer.toString(song.getWeek());
    }

    public int getPositionAsInt() {
        return positionAsInt;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getPeak() {
        return peak;
    }

    public void setPeak(String peak) {
        this.peak = peak;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public void setPositionAsInt(int positionAsInt) {
        this.positionAsInt = positionAsInt;
    }
}
