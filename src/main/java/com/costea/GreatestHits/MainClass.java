package com.costea.GreatestHits;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MainClass {
    static ChartSimulator chartSimulator;

    public static File getFileFromResources(String fileName) throws IOException {
        try {
            return new File(ChartSimulator.class.getResource(fileName).getFile());
        }
        catch(NullPointerException ex)
        {
            throw new IOException();
        }
    }

    public static void main(String[] args) {
        try {
/*
            Scanner reader = new Scanner(getFileFromResources("chartData.json"));
            for(int i=1;i<=10;i++)
                System.out.println(reader.nextLine());
*/

            chartSimulator = new ChartSimulator();
            for (int i = 1; i <= 10; i++)
                chartSimulator.nextWeek();
            chartSimulator.writeBiggestSongs();

            ObjectMapper mapper = new ObjectMapper();

            FileWriter jsonWriter=new FileWriter(getFileFromResources("chartData.json"));
            jsonWriter.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartSimulator.getAllCharts()));
            jsonWriter.close();

            jsonWriter=new FileWriter(getFileFromResources("artistData.json"));
            jsonWriter.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartSimulator.getArtists()));
            jsonWriter.close();

            jsonWriter=new FileWriter(getFileFromResources("songData.json"));
            jsonWriter.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartSimulator.getSongs()));
            jsonWriter.close();
        }
        catch(IOException ex)
        {
            System.out.println("File reading/writing didn't work!");
            ex.printStackTrace();
        }
    }
}
