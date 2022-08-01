package com.costea.GreatestHits;

import java.util.ArrayList;

public class AllCharts extends ArrayList<Chart> {
    private final ArrayList<Song> songs;
    private final ArrayList<Artist> artists;

    public AllCharts(ArrayList<Song> songs, ArrayList<Artist> artists) {
        this.songs=songs;
        this.artists=artists;
    }
}
