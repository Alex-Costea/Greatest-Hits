package GreatestHits;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.File;
import static java.lang.Math.min;


class MainClass {
    private static final int nrChartEntries =20; //chart entries to be shown every week
    private static final int weeks=52; //number of weeks shown
    private static final ArrayList<String> maleNames = new ArrayList<>(); //list of male names
    private static final ArrayList<String> femaleNames = new ArrayList<>(); //list of female names
    private static final ArrayList<String> lastNames = new ArrayList<>(); //list of family names
    private static final ArrayList<String> titles = new ArrayList<>(); //list of song titles
    public static Random ran = new Random(); // random number generator
    private static final TreeMap<Integer,Integer> peaks=new TreeMap<>(); //peaks of songs
    private static final ArrayList<Artist> artists=new ArrayList<>(); //list of all artists
    private static final ArrayList<Song> songs=new ArrayList<>(); //list of all songs
    private static final HashMap<Integer,Song> songsById=new HashMap<>(); //get a song by ID
    private static final int nrArtists=500; //total number of songs
    private static HashMap<Integer,Integer> lastWeekPos = new HashMap<>(); //positions last week
    private static FileWriter fw; //writing results to file


    //initialize artist and song titles by reading them from the file
    static void InitNames() throws FileNotFoundException {
        Scanner scanner;

        //Male
        File maleFile=new File("male");
        scanner = new Scanner(maleFile);
        while (scanner.hasNextLine())
            maleNames.add(scanner.nextLine());

        //Female
        File femaleFile=new File("female");
        scanner = new Scanner(femaleFile);
        while (scanner.hasNextLine())
            femaleNames.add(scanner.nextLine());

        //Last
        File lastFile=new File("last");
        scanner = new Scanner(lastFile);
        while (scanner.hasNextLine())
            lastNames.add(scanner.nextLine());

        //Titles
        File titlesFile=new File("titles");
        scanner = new Scanner(titlesFile);
        while (scanner.hasNextLine())
            titles.add(scanner.nextLine());
    }

    //properly capitalize artist names
    static private String capitalize(String x)
    {
        return x.substring(0,1).toUpperCase()+
                x.substring(1).toLowerCase();
    }

    //pick a title at random
    static private String pickSongTitle()
    {
        int i=ran.nextInt(titles.size());
        return titles.get(i);
    }

    //pick an artist name at random
    static String pickArtistName()
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
    static void addSongs(int nr)
    {
        double minSongProb=1.0*nr/nrArtists;
        for(Artist artist:artists)
        {
            double prob=artist.songReleaseProb();
            if(prob<minSongProb)
            {
                Song newSong=new Song(pickSongTitle(),artist);
                songs.add(newSong);
                artist.songsReleased.add(newSong);
            }
        }
    }

    //format the chart entry properly for printing to file
    static String FormatChartEntry(int position,
                                   String artistName,
                                   String songName,
                                   int lastWeek,
                                   int points,
                                   int weeks,
                                   boolean newAppearance)
    {
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
        return String.format("#%d %s -- %s (%s) (points: %d; week: %d)\n",
                position,artistName,songName,insideParen,points,weeks);
    }

    //format the year-end entry properly for printing to file
    static String FormatYearEndEntry(int position,
                                     String artistName,
                                     String songName,
                                     int peak)
    {
        return String.format("#%d %s -- %s (peak: %d)\n",
                position,artistName,songName,peak);
    }

    //move to the next week
    static void nextWeek(int i) throws IOException {
        for(Song song : songs) {
            song.addWeek();
            if(!songsById.containsKey(song.ID))
                songsById.put(song.ID,song);
        }
        TreeMap<Double,Song> sortedSongs =new TreeMap<>();
        for (Song song : songs)
            sortedSongs.put(song.points,song);
        NavigableMap<Double, Song> songsList=sortedSongs.descendingMap();
        int j=0;
        if(i>0) {
            fw.write("Week ");
            fw.write(String.valueOf(i));
            fw.write("\n");
            for (Map.Entry<Double, Song> entry : songsList.entrySet()) {
                Song currentSong = entry.getValue();
                j++;
                if (j <= nrChartEntries) {
                    //format
                    int lastPos;
                    lastPos = lastWeekPos.getOrDefault(currentSong.ID, -1);
                    if(lastPos> nrChartEntries) lastPos=-1;
                    fw.write(FormatChartEntry(j,
                            currentSong.artist.name,
                            currentSong.name,
                            lastPos,
                            (int)currentSong.points,
                            currentSong.week,
                            peaks.getOrDefault(currentSong.ID,999)> nrChartEntries));
                }
                currentSong.addFullPoints();
                peaks.put(currentSong.ID, min(peaks.getOrDefault(currentSong.ID,999), j));
            }
            fw.write("\n");
        }

        songs.removeIf(x -> x.points<1);
        addSongs(20);
        if(i>=0)
        {
            lastWeekPos=new HashMap<>();
            j=0;
            for (Map.Entry<Double, Song> entry : songsList.entrySet())
            {
                j++;
                lastWeekPos.put(entry.getValue().ID,j);
            }
        }
    }

    private static void displayYearEnd() throws IOException {
        fw.write("Year End\n");
        TreeMap<Double,Integer> fullPointsOrdered = new TreeMap<>();
        for(Song song:songs)
            if(song.getFullPoints()>0)
                fullPointsOrdered.put(song.getFullPoints(),song.ID);
        NavigableMap<Double, Integer> yearEnd=fullPointsOrdered.descendingMap();
        int i=0;
        for (Map.Entry<Double, Integer> entry : yearEnd.entrySet()) {
            i++;
            if(i>40) break;
            Song currentSong=songsById.get(entry.getValue());
            fw.write(FormatYearEndEntry(i,
                    artists.get(currentSong.artist.ID).name,
                    currentSong.name,
                    peaks.getOrDefault(entry.getValue(),999)));
        }
    }

    private static void initSongs()
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

    public static void main(String[] args) {
        try {
            InitNames();
            //read file
            File file=new File("charts.txt");
            fw = new FileWriter(file);
            initSongs();
            //simulate the charts for a year
            for(int i=-49;i<=weeks;i++)
                nextWeek(i);
            displayYearEnd();
            fw.close();
        } catch (IOException e) {
            System.out.println("File reading/writing didn't work!");
        }
    }
}