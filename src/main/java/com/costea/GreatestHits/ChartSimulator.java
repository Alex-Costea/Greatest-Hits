package com.costea.GreatestHits;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.Math.min;


class ChartSimulator {
    private static final int nrChartEntries = 20; //chart entries to be shown every week
    private static final ArrayList<String> maleNames = new ArrayList<>(); //list of male names
    private static final ArrayList<String> femaleNames = new ArrayList<>(); //list of female names
    private static final ArrayList<String> lastNames = new ArrayList<>(); //list of family names
    private static final ArrayList<String> titles = new ArrayList<>(); //list of song titles
    public static Random ran = new Random(); // random number generator
    private static final int nrArtists=500; //total number of songs
    private final ArrayList<Artist> artists=new ArrayList<>(); //list of all artists
    private final ArrayList<Song> songs=new ArrayList<>(); //list of all songs
    private final FileWriter fw; //writing results to file
    private int currentWeek=-50;
    private ArrayList<ChartEntry> currentChartEntries;

    public AllCharts getAllCharts() {
        return allCharts;
    }

    private final AllCharts allCharts=new AllCharts(songs,artists);

    private File getFileFromResources(String fileName) throws IOException {
        try {
            return new File(getClass().getResource(fileName).getFile());
        }
        catch(NullPointerException ex)
        {
            throw new IOException();
        }
    }

    //initialize artist and song titles by reading them from the file
    private void InitNames() throws IOException {
        Scanner scanner;

        //Male
        File maleFile=getFileFromResources("male");
        scanner = new Scanner(maleFile);
        while (scanner.hasNextLine())
            maleNames.add(scanner.nextLine());

        //Female
        File femaleFile=getFileFromResources("female");
        scanner = new Scanner(femaleFile);
        while (scanner.hasNextLine())
            femaleNames.add(scanner.nextLine());

        //Last
        File lastFile=getFileFromResources("last");
        scanner = new Scanner(lastFile);
        while (scanner.hasNextLine())
            lastNames.add(scanner.nextLine());

        //Titles
        File titlesFile=getFileFromResources("titles");
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

    //add approximately nr number of songs. not exact due to probabilities
    private void addSongs(int nr)
    {
        double minSongProb=1-1.0*nr/nrArtists;
        for(Artist artist:artists)
        {
            double prob=artist.songReleaseProb(currentWeek);
            if(prob>minSongProb)
            {
                Song newSong=new Song(pickSongTitle(),artist);
                songs.add(newSong);
                artist.getSongsReleased().add(newSong);
            }
        }
    }

    //format the chart entry properly for printing to file
    private void addChartEntry(int position,
                                 String artistName,
                                 String songName,
                                 int lastWeek,
                                 int points,
                                 int weeks,
                                 boolean newAppearance) throws IOException {
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
        String format="#%d %s -- %s (%s) (points: %d; week: %d)\n";
        currentChartEntries.add(new ChartEntry(position,artistName,songName,insideParen,points,weeks));
        fw.write(String.format(format,position,artistName,songName,insideParen,points,weeks));
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
            fw.write("Week ");
            fw.write(String.valueOf(currentWeek));
            fw.write("\n");
            currentChartEntries=new ArrayList<>();
            for (Map.Entry<Double, Song> entry : songsList.entrySet()) {
                Song currentSong = entry.getValue();
                j++;
                if (j <= nrChartEntries) {
                    //format
                    int lastPos = currentSong.getLastWeek();
                    if(lastPos> nrChartEntries) lastPos=-1;
                    addChartEntry(j,
                        currentSong.getArtist().getName(),
                        currentSong.getName(),
                        lastPos,
                        (int) currentSong.getPoints(),
                        currentSong.getWeek(),
                            currentSong.getLastWeek() > nrChartEntries);
                }
                currentSong.addFullPoints();
                currentSong.setPeak(min(currentSong.getPeak(),j));
            }
            allCharts.add(new Chart(currentWeek,currentChartEntries));
            fw.write("\n");
        }
        songs.removeIf(x -> x.getPoints() <1);
        addSongs(20);
        if(currentWeek>=0)
        {
            j=0;
            for (Map.Entry<Double, Song> entry : songsList.entrySet())
            {
                j++;
                entry.getValue().setLastWeek(j);
            }
        }
    }


    //TODO: additional functionality, list of year ends, year end = last 52 weeks
    //TODO: not strictly necessary for MVP
    public void displayYearEnd() throws IOException {
        fw.write("Year End\n");
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
            fw.write(FormatYearEndEntry(i,
                    currentSong.getArtist().getName(),
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
        addSongs(100);
    }

    public void closeWriter() throws IOException {
        fw.close();
    }

    public ChartSimulator() throws IOException {
        InitNames();
        File file=getFileFromResources("charts.txt");
        fw = new FileWriter(file);
        initSongs();
        //simulate 50 weeks before the first week, as to properly populate the charts
        for(int i=0;i<50;i++)
            nextWeek();
    }

}