package com.costea.GreatestHits;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.costea.GreatestHits.GreatestHitsApplication.getInputStream;
import static java.lang.Math.min;


class ChartSimulator {
    private static final int nrChartEntries = 20; //chart entries to be shown every week
    private static final ArrayList<String> maleNames = new ArrayList<>(); //list of male names
    private static final ArrayList<String> femaleNames = new ArrayList<>(); //list of female names
    private static final ArrayList<String> lastNames = new ArrayList<>(); //list of family names
    private static final ArrayList<String> titles = new ArrayList<>(); //list of song titles
    public static Random ran = new Random(); // random number generator
    private static final int nrArtists=500; //total number of songs
    private ArrayList<Artist> artists=new ArrayList<>(); //list of all artists

    private ArrayList<Song> songs=new ArrayList<>(); //list of all songs
    private int currentWeek=-50;
    private List<Chart> allCharts = new ArrayList<>();

    public List<Chart> getAllCharts() {
        return allCharts;
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    //initialize artist and song titles by reading them from the file
    private void InitNames() throws IOException {
        Scanner scanner;

        //Male
        InputStream maleFile= getInputStream("male");
        scanner = new Scanner(maleFile);
        while (scanner.hasNextLine())
            maleNames.add(scanner.nextLine());

        //Female
        InputStream femaleFile= getInputStream("female");
        scanner = new Scanner(femaleFile);
        while (scanner.hasNextLine())
            femaleNames.add(scanner.nextLine());

        //Last
        InputStream lastFile= getInputStream("last");
        scanner = new Scanner(lastFile);
        while (scanner.hasNextLine())
            lastNames.add(scanner.nextLine());

        //Titles
        InputStream titlesFile= getInputStream("titles");
        scanner = new Scanner(titlesFile);
        while (scanner.hasNextLine())
            titles.add(scanner.nextLine());
    }

    //properly capitalize artist names
    private static String capitalize(String x)
    {
        return x.substring(0,1).toUpperCase()+
                x.substring(1).toLowerCase();
    }

    //pick a title at random
    private String pickSongTitle()
    {
        int i=ran.nextInt(titles.size());
        return titles.get(i);
    }

    //pick an artist name at random
    private String pickArtistName()
    {
        String name;
        boolean midName=false;
        if(ran.nextInt(2)==1) {
            int i=ran.nextInt(maleNames.size());
            name=capitalize(maleNames.get(i));
            if(ran.nextInt(5)==0) {
                midName=true;
                int j=ran.nextInt(maleNames.size());
                name += " " + capitalize(maleNames.get(j));
            }
        }
        else {
            int i=ran.nextInt(femaleNames.size());
            name=capitalize(femaleNames.get(i));
            if(ran.nextInt(5)==0) {
                midName=true;
                int j=ran.nextInt(femaleNames.size());
                name += " " + capitalize(femaleNames.get(j));
            }
        }
        if(!midName && ran.nextInt(3)>0)
        {
            int i = ran.nextInt(lastNames.size());
            name += " " + capitalize(lastNames.get(i));
        }
        return name;
    }

    private void addSongs()
    {
        double minSongProb=1-20.0/nrArtists;
        for(Artist artist:artists)
        {
            double prob=artist.songReleaseProb(currentWeek);
            if(prob>minSongProb)
            {
                Song newSong=new Song(pickSongTitle(),artist);
                songs.add(newSong);
            }
        }
    }

    //format the chart entry properly for printing to file
    private ChartEntry createChartEntry(int position,
                                        String artistName,
                                        String songName,
                                        int lastWeek,
                                        int points,
                                        int weeks,
                                        boolean newAppearance,
                                        int peak) {
        String insideParen;
        if(lastWeek==-1 && newAppearance)
            insideParen="new";
        else if(lastWeek==-1)
            insideParen="re-entry";
        else if(lastWeek==position)
            insideParen="=";
        else if(lastWeek>position)
            insideParen=String.format("+%d",lastWeek-position);
        else insideParen=String.format("%d",lastWeek-position);
        return new ChartEntry(position,artistName,songName,insideParen,points,weeks,peak);
    }

    //format the year-end entry properly for printing to file
    private String FormatYearEndEntry(int position,
                                     String artistName,
                                     String songName,
                                     int peak)
    {
        return String.format("#%d %s -- %s (peak: %d)\n",
                position,artistName,songName,peak);
    }

    //move charts to the next week
    public void nextWeek() throws IOException {
        currentWeek++;
        for(Song song : songs)
            song.addWeek();
        TreeMap<Double,Song> sortedSongs =new TreeMap<>();
        for (Song song : songs)
            sortedSongs.put(song.getPoints(),song);
        NavigableMap<Double, Song> songsList=sortedSongs.descendingMap();
        int j=0;
        if(currentWeek>0) {
            ArrayList<ChartEntry> currentChartEntries=new ArrayList<>();
            for (Map.Entry<Double, Song> entry : songsList.entrySet()) {
                Song currentSong = entry.getValue();
                j++;
                currentSong.setPeak(min(currentSong.getPeak(),j));
                currentSong.addFullPoints();
                if (j <= nrChartEntries) {
                    //format
                    int lastPos = currentSong.getCurrentPosition();
                    if(lastPos> nrChartEntries) lastPos=-1;
                    currentChartEntries.add(createChartEntry(j,
                        currentSong.getAristName(),
                        currentSong.getName(),
                        lastPos,
                        (int) currentSong.getPoints(),
                        currentSong.getWeek(),
          currentSong.getCurrentPosition() > nrChartEntries,
                        currentSong.getPeak()));
                }
            }
            allCharts.add(new Chart(currentWeek,currentChartEntries));
        }
        songs.removeIf(x -> x.getPoints() <1);
        addSongs();
        if(currentWeek>=0)
        {
            j=0;
            for (Map.Entry<Double, Song> entry : songsList.entrySet())
            {
                j++;
                entry.getValue().setCurrentPosition(j);
            }
        }
    }


    //TODO: additional functionality, list of year ends, year end = last 52 weeks
    //TODO: not strictly necessary for MVP
    public void writeBiggestSongs() {
        System.out.println("Biggest Songs\n");
        TreeMap<Double,Song> fullPointsOrdered = new TreeMap<>();
        for(Song song:songs)
            if(song.getFullPoints()>0)
                fullPointsOrdered.put(song.getFullPoints(), song);
        NavigableMap<Double, Song> yearEnd=fullPointsOrdered.descendingMap();
        int i=0;
        for (Map.Entry<Double, Song> entry : yearEnd.entrySet()) {
            i++;
            if(i>40) break;
            Song currentSong=entry.getValue();
            System.out.println(FormatYearEndEntry(i,
                    currentSong.getAristName(),
                    currentSong.getName(),
                    currentSong.getPeak()));
        }
    }

    private void initSongs()
    {
        HashSet<String> artistNames=new HashSet<>();
        for(int i=0;i<nrArtists;i++) {
            //make sure there are no duplicates
            String artistName= pickArtistName();
            while(artistNames.contains(artistName))
                artistName= pickArtistName();

            artistNames.add(artistName);
            artists.add(new Artist(artistName,i));
        }
        for(int i=1;i<=5;i++)
            addSongs();
    }

    public ChartSimulator(ArrayList<Artist> artists,ArrayList<Song> songs, ArrayList<Chart> chartData) throws IOException {
        InitNames();
        this.artists=artists;
        this.songs=songs;
        this.allCharts=chartData;
        Song.totalID=songs.get(songs.size()-1).getID()+1;
        currentWeek=allCharts.get(allCharts.size()-1).getWeek();
        for(Song song:songs)
            song.transmitArtistData(artists);
    }

    public ChartSimulator() throws IOException {
        InitNames();
        initSongs();
        for(Song song:songs)
            song.transmitArtistData(artists);
        //simulate 50 weeks before the first week, as to properly populate the charts
        for (int i = 0; i < 50; i++)
            nextWeek();
    }

}