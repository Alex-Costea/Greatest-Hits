package com.costea.GreatestHits;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

import static com.costea.GreatestHits.GreatestHitsApplication.chartSimulator;
import static com.costea.GreatestHits.GreatestHitsApplication.mapper;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class ChartsController {

    @GetMapping("/")
    public String getChartEntries(Model model){
        model.addAttribute("chart", chartSimulator.getAllCharts());
        return "index";
    }

    //TODO: Make an actual page for it
    @GetMapping(value="/artist/{name}")
    @ResponseBody
    public String getArtistByName(@PathVariable("name") String artistName)
    {
        var artists =chartSimulator.getArtists();
        for(Artist artist : artists)
            if(Objects.equals(artist.getName(), artistName))
            {
                StringBuilder sb = new StringBuilder("Name: " + artist.getName() + "; ID: " + artist.getID());
                int artistId=artist.getID();
                for(Song song: chartSimulator.getSongs())
                    if(song.getArtistID()==artistId)
                        sb.append("\n<br />Song: ").append(song.getName()).append("; ID: ").append(song.getID());
                return sb.toString();
            }
        throw new ResponseStatusException(NOT_FOUND, "Artist not found!");
    }

    //TODO: Solve in case multiple songs with same artist and song title
    //TODO: Also make an actual page for it
    @GetMapping(value="/song/{artist}/{songName}")
    @ResponseBody
    public String getSongByID(@PathVariable("artist") String artistName,
                              @PathVariable("songName") String songName)
    {
        StringBuilder sb=new StringBuilder("Artist: " + artistName + "; Title: " + songName);
        int currentChartWeek=chartSimulator.getCurrentWeek();
        boolean foundEntries=false;
        for(Chart chart :chartSimulator.getAllCharts())
            for (ChartEntry chartEntry : chart.getChartEntries())
                if (chartEntry.getArtistName().equals(artistName) && chartEntry.getSongName().equals(songName))
                {
                    sb.append("\n<br />Week: ").append(chart.getWeek()).append("; Position: ").append(chartEntry.getPosition());
                    foundEntries=true;
                }
        if(foundEntries) return sb.toString();
        else throw new ResponseStatusException(NOT_FOUND, "Song not found!");
    }

    @GetMapping(value="/data.json")
    @ResponseBody
    public ResponseEntity<String> readJSON() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartSimulator.getAllCharts());
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.CREATED);
        }
        catch (JsonProcessingException ex)
        {
            throw new ResponseStatusException(NOT_FOUND);
        }

    }
}
