import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.File;

class Artist {
    String name;
    ArrayList<Song> songs = new ArrayList<>();
    Artist(String name)
    {
        this.name=name;
    }

}

class Song {
    String name;
    static Random ran = new Random();
    double decay;
    double popularity;
    double quality;
    double fanReception;
    static int totalID=0;
    int currentID;
    int week;
    double points;

    Song(String name)
    {
        this.name=name;
        this.decay=ran.nextInt(90,97)*0.01;
        this.popularity=Math.pow(ran.nextInt(0,91)*0.01,4);
        this.quality=ran.nextInt(100,1001)/100.0;
        this.fanReception=this.quality;
        totalID+=1;
        this.currentID=totalID;
        this.week=0;
    }

    void addWeek()
    {
        this.week++;
        //I have no idea how I came up with this
        double hype=Math.pow(10,ran.nextInt(40,161)/100.0)
                /Math.pow(10,1.6)/2.5*Math.pow(0.99,this.week-1);

        this.popularity=1-(1-this.popularity)*(1-hype);
        this.fanReception=this.fanReception*(this.decay+ran.nextInt(21)*0.001);

        //again, no clue
        this.points=Math.pow(this.popularity*this.fanReception/10,1.5)*100;
        this.points=Math.pow(this.points,1.5)/((100/this.points))*2;
    }
}

class GreatestHits {
    static final int nr_top=20;
    static final int weeks=52;
    static ArrayList<String> maleNames = new ArrayList<>();
    static ArrayList<String> femaleNames = new ArrayList<>();
    static ArrayList<String> lastNames = new ArrayList<>();
    static ArrayList<String> titles = new ArrayList<>();
    static Random ran = new Random();
    HashMap<Integer,Double> fullPoints=new HashMap<>();
    HashMap<Integer,Double> peaks=new HashMap<>();
    static ArrayList<Artist> artists=new ArrayList<>();
    static ArrayList<Song> songs=new ArrayList<>();


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

    static private String capitalize(String x)
    {
        return x.substring(0,1).toUpperCase()+
                x.substring(1).toLowerCase();
    }

    static private String pickTitle()
    {
        int i=ran.nextInt(titles.size());
        return titles.get(i);
    }

    static String ArtistName()
    {
        String name;
        if(ran.nextInt(2)==1) {
            int i=ran.nextInt(maleNames.size());
            name=capitalize(maleNames.get(i));
            if(ran.nextInt(5)==0) {
                int j=ran.nextInt(maleNames.size());
                name += " " + capitalize(maleNames.get(j));
            }
        }
        else {
            int i=ran.nextInt(femaleNames.size());
            name=capitalize(femaleNames.get(i));
            if(ran.nextInt(5)==0) {
                int j=ran.nextInt(femaleNames.size());
                name += " " + capitalize(femaleNames.get(j));
            }
        }
        int i=ran.nextInt(lastNames.size());
        name+=" "+capitalize(lastNames.get(i));
        return name;
    }

    public static void main(String[] args) {
        try {
            InitNames();

            File file=new File("charts");
            FileWriter fw = new FileWriter(file);

            for(int i=0;i<500;i++)
                artists.add(new Artist(ArtistName()));

            for(int i=0;i<100;i++) {
                songs.add(new Song(pickTitle()));
                songs.get(i).addWeek();
            }

            HashMap<Integer,Integer> lastPos =new HashMap<>();

            for(int i=-49;i<=52;i++) {
                //to be continued
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("File reading didn't work!");
        }
    }
}