package com.costea.GreatestHits;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

@SpringBootApplication
public class GreatestHitsApplication {
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
		SpringApplication.run(GreatestHitsApplication.class, args);
		ObjectMapper mapper = new ObjectMapper();
		try {
			if(args.length>0 && args[0].equals("init"))
			{
				chartSimulator = new ChartSimulator();
			}
			else
			{
				TypeFactory t = TypeFactory.defaultInstance();
				ArrayList<Artist> artistList = mapper.readValue(getFileFromResources("artistData.json"),
						t.constructCollectionType(ArrayList.class,Artist.class));
				ArrayList<Song> songList = mapper.readValue(getFileFromResources("songData.json"),
						t.constructCollectionType(ArrayList.class,Song.class));
				ArrayList<Chart> chartData = mapper.readValue(getFileFromResources("chartData.json"),
						t.constructCollectionType(ArrayList.class,Chart.class));
				chartSimulator = new ChartSimulator(artistList,songList,chartData);
			}
			chartSimulator.nextWeek();
			//save data
			FileWriter jsonWriter = new FileWriter(getFileFromResources("chartData.json"));
			jsonWriter.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartSimulator.getAllCharts()));
			jsonWriter.close();

			jsonWriter = new FileWriter(getFileFromResources("artistData.json"));
			jsonWriter.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartSimulator.getArtists()));
			jsonWriter.close();

			jsonWriter = new FileWriter(getFileFromResources("songData.json"));
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
