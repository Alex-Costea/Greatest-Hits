package GreatestHits;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.File;
import static java.lang.Math.min;


class ChartSimulator {
    private static final int nrChartEntries =20; //chart entries to be shown every week
    private static final ArrayList<String> maleNames = new ArrayList<>(); //list of male names
    private static final ArrayList<String> femaleNames = new ArrayList<>(); //list of female names
    private static final ArrayList<String> lastNames = new ArrayList<>(); //list of family names
    private static final ArrayList<String> titles = new ArrayList<>(); //list of song titles
    public static Random ran = new Random(); // random number generator
    private static final int nrArtists=500; //total number of songs
    private final TreeMap<Integer,Integer> peaks=new TreeMap<>(); //peaks of songs
    private final ArrayList<Artist> artists=new ArrayList<>(); //list of all artists
    private final ArrayList<Song> songs=new ArrayList<>(); //list of all songs
    private final HashMap<Integer,Song> songsById=new HashMap<>(); //get a song by ID
    private HashMap<Integer,Integer> lastWeekPos = new HashMap<>(); //positions last week
    private final FileWriter fw; //writing results to file
    private int currentWeek=-50;

    //initialize artist and song titles by reading them from the file
    private void InitNames() throws FileNotFoundException {
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

    int sumSongsReleased=0;
    int nrTimesSongsReleased=0;
    //add approximately nr number of songs. not exact due to probabilities
    private void addSongs(int nr)
    {
        double minSongProb=1-1.0*nr/nrArtists;
        int songsReleased=0;
        for(Artist artist:artists)
        {
            double prob;
            prob=artist.songReleaseProb(currentWeek);
            if(prob>minSongProb)
            {
                songsReleased++;
                Song newSong=new Song(pickSongTitle(),artist);
                songs.add(newSong);
                artist.getSongsReleased().add(newSong);
            }
        }
        //System.out.println(nr+" "+songsReleased);
        sumSongsReleased+=songsReleased;
        nrTimesSongsReleased+=1;
    }

    //format the chart entry properly for printing to file
    private String FormatChartEntry(int position,
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
        for(Song song : songs) {
            song.addWeek();
            if(!songsById.containsKey(song.getID()))
                songsById.put(song.getID(),song);
        }
        TreeMap<Double,Song> sortedSongs =new TreeMap<>();
        for (Song song : songs)
            sortedSongs.put(song.getPoints(),song);
        NavigableMap<Double, Song> songsList=sortedSongs.descendingMap();
        int j=0;
        if(currentWeek>0) {
            fw.write("Week ");
            fw.write(String.valueOf(currentWeek));
            fw.write("\n");
            for (Map.Entry<Double, Song> entry : songsList.entrySet()) {
                Song currentSong = entry.getValue();
                j++;
                if (j <= nrChartEntries) {
                    //format
                    int lastPos;
                    lastPos = lastWeekPos.getOrDefault(currentSong.getID(), -1);
                    if(lastPos> nrChartEntries) lastPos=-1;
                    fw.write(FormatChartEntry(j,
                            currentSong.getArtist().getName(),
                            currentSong.getName(),
                            lastPos,
                            (int) currentSong.getPoints(),
                            currentSong.getWeek(),
                            peaks.getOrDefault(currentSong.getID(),999)> nrChartEntries));
                }
                currentSong.addFullPoints();
                peaks.put(currentSong.getID(), min(peaks.getOrDefault(currentSong.getID(),999), j));
            }
            fw.write("\n");
        }
        songs.removeIf(x -> x.getPoints() <1);
        addSongs(20);
        if(currentWeek>=0)
        {
            lastWeekPos=new HashMap<>();
            j=0;
            for (Map.Entry<Double, Song> entry : songsList.entrySet())
            {
                j++;
                lastWeekPos.put(entry.getValue().getID(),j);
            }
        }
    }

    public void displayYearEnd() throws IOException {
        fw.write("Year End\n");
        TreeMap<Double,Integer> fullPointsOrdered = new TreeMap<>();
        for(Song song:songs)
            if(song.getFullPoints()>0)
                fullPointsOrdered.put(song.getFullPoints(), song.getID());
        NavigableMap<Double, Integer> yearEnd=fullPointsOrdered.descendingMap();
        int i=0;
        for (Map.Entry<Double, Integer> entry : yearEnd.entrySet()) {
            i++;
            if(i>40) break;
            Song currentSong=songsById.get(entry.getValue());
            fw.write(FormatYearEndEntry(i,
                    artists.get(currentSong.getArtist().getID()).getName(),
                    currentSong.getName(),
                    peaks.getOrDefault(entry.getValue(),999)));
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
        System.out.println(sumSongsReleased/(double)nrTimesSongsReleased);
        fw.close();
    }

    public ChartSimulator() throws IOException {
        InitNames();
        File file=new File("charts.txt");
        fw = new FileWriter(file);
        initSongs();
        //simulate 50 weeks before the first week, as to properly populate the charts
        for(int i=0;i<50;i++)
            nextWeek();
    }

}