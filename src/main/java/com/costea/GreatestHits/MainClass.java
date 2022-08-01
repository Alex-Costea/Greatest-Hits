package com.costea.GreatestHits;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
            chartSimulator = new ChartSimulator();
            for (int i = 1; i <= 52; i++)
                chartSimulator.nextWeek();
            chartSimulator.displayYearEnd();
            chartSimulator.closeWriter();

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
