package com.costea.GreatestHits;

import com.costea.GreatestHits.DataObjects.Artist;
import com.costea.GreatestHits.DataObjects.Chart;
import com.costea.GreatestHits.DataObjects.Song;
import com.costea.GreatestHits.Processes.ChartSimulationProcess;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.costea.GreatestHits.Statics.Statics.chartSimulationProcess;
import static com.costea.GreatestHits.Statics.Statics.mapper;

@SpringBootApplication
public class GreatestHitsApplication {


	public static File accessFile(String fileName) throws IOException {
		File file = new File("../gh_data/" + fileName);
		file.getParentFile().mkdirs();
		file.createNewFile();
		return file;
	}

	public static InputStream getInputStream(String fileName) throws IOException {
		return (new ClassPathResource("com/costea/" + fileName)).getInputStream();
	}

	public static void main(String[] args) {
		SpringApplication.run(GreatestHitsApplication.class, args);
		mapper.findAndRegisterModules();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			if(args.length>0 && args[0].equals("init"))
			{
				System.out.println("Initializing data");
				chartSimulationProcess = new ChartSimulationProcess();
				for(int i=1;i<=1;i++)
					chartSimulationProcess.nextWeek();
			}
			else
			{
				System.out.println("Re-reading data");
				TypeFactory t = TypeFactory.defaultInstance();
				ArrayList<Artist> artistList = mapper.readValue(accessFile("artistData.json"),
						t.constructCollectionType(ArrayList.class,Artist.class));
				ArrayList<Song> songList = mapper.readValue(accessFile("songData.json"),
						t.constructCollectionType(ArrayList.class,Song.class));
				ArrayList<Chart> chartData = mapper.readValue(accessFile("chartData.json"),
						t.constructCollectionType(ArrayList.class,Chart.class));
				chartSimulationProcess = new ChartSimulationProcess(artistList,songList,chartData);
				if(args.length>0 && args[0].equals("addweek"))
				{
					chartSimulationProcess.nextWeek();
				}
			}
			System.out.println("Saving data");
			//save data
			FileWriter jsonWriter = new FileWriter(accessFile("chartData.json"));
			jsonWriter.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartSimulationProcess.getAllCharts()));
			jsonWriter.close();

			jsonWriter = new FileWriter(accessFile("artistData.json"));
			jsonWriter.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartSimulationProcess.getArtists()));
			jsonWriter.close();

			jsonWriter = new FileWriter(accessFile("songData.json"));
			jsonWriter.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartSimulationProcess.getSongs()));
			jsonWriter.close();

		}
		catch(IOException ex)
		{
			System.out.println("File reading/writing didn't work!");
			ex.printStackTrace();
		}
		System.out.println("Initializing complete!");
	}

}
