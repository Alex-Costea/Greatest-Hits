package com.costea.GreatestHits;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@SpringBootApplication
public class GreatestHitsApplication {
	static ChartSimulator chartSimulator;

	public static File accessFile(String fileName) throws IOException {
		File file = new File("../gh_data/" + fileName);
		file.getParentFile().mkdirs();
		file.createNewFile();
		return file;
	}

	public static InputStream getInputStream(String fileName) throws IOException {
		return (new ClassPathResource("com/costea/GreatestHits/" + fileName)).getInputStream();
	}

	public static void main(String[] args) {
		SpringApplication.run(GreatestHitsApplication.class, args);
		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		try {
			if(args.length>0 && args[0].equals("init"))
			{
				chartSimulator = new ChartSimulator();
				chartSimulator.nextWeek();
			}
			else
			{
				TypeFactory t = TypeFactory.defaultInstance();
				ArrayList<Artist> artistList = mapper.readValue(accessFile("artistData.json"),
						t.constructCollectionType(ArrayList.class,Artist.class));
				ArrayList<Song> songList = mapper.readValue(accessFile("songData.json"),
						t.constructCollectionType(ArrayList.class,Song.class));
				ArrayList<Chart> chartData = mapper.readValue(accessFile("chartData.json"),
						t.constructCollectionType(ArrayList.class,Chart.class));
				chartSimulator = new ChartSimulator(artistList,songList,chartData);
				if(args.length>0 && args[0].equals("addweek"))
				{
					chartSimulator.nextWeek();
				}
			}
			//save data
			FileWriter jsonWriter = new FileWriter(accessFile("chartData.json"));
			jsonWriter.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartSimulator.getAllCharts()));
			jsonWriter.close();

			jsonWriter = new FileWriter(accessFile("artistData.json"));
			jsonWriter.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartSimulator.getArtists()));
			jsonWriter.close();

			jsonWriter = new FileWriter(accessFile("songData.json"));
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
