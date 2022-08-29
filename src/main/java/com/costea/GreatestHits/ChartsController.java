package com.costea.GreatestHits;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    //TODO: Redirect from /artist/song/week to /song/id/
    //TODO: Also make an actual page for it
    @GetMapping(value="/song/{ID}")
    @ResponseBody
    public String getSongByName(@PathVariable("ID") int ID)
    {
        var songs =chartSimulator.getSongs();
        System.out.println(ID);
        for(Song song : songs)
            if(ID==song.getID())
            {
                StringBuilder sb=new StringBuilder("Artist: " + song.getAristName() + "; Title: " + song.getName() + "; ID: " + song.getID());
                String artistName=song.getAristName();
                String songName=song.getName();
                int songWeek=song.getWeek();
                int currentChartWeek=chartSimulator.getAllCharts().get(chartSimulator.getAllCharts().size()-1).getWeek();
                int i = currentChartWeek-1;
                for(Chart chart :chartSimulator.getAllCharts()) {
                    for (ChartEntry chartEntry : chart.getChartEntries())
                        if (chartEntry.getArtistName().equals(artistName)
                                && chartEntry.getSongName().equals(songName)
                                && chartEntry.getWeeks() == songWeek - i)
                            sb.append("\n<br />Week: ").append(chart.getWeek()).append("; Position: ").append(chartEntry.getPosition());
                    i--;
                }
                return sb.toString();

            }
        throw new ResponseStatusException(NOT_FOUND, "Song not found!");
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
