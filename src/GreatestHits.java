import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.File;
import java.util.Random;
import java.util.Scanner;

class Artist {
    String name="";
}

class Song {
}

class GreatestHits {
    static ArrayList<String> maleNames = new ArrayList<>();
    static ArrayList<String> femaleNames = new ArrayList<>();
    static ArrayList<String> lastNames = new ArrayList<>();
    static ArrayList<String> titles = new ArrayList<>();
    static Random ran = new Random();

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

    static String capitalize(String x)
    {
        return x.substring(0,1).toUpperCase()+
                x.substring(1).toLowerCase();
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
        int i=ran.nextInt(femaleNames.size());
        name+=" "+capitalize(lastNames.get(i));
        return name;
    }

    public static void main(String[] args) {
        try {
            InitNames();
            System.out.println(ArtistName());
        } catch (FileNotFoundException e) {
            System.out.println("File reading didn't work!");
        }
    }
}