package com.costea.GreatestHits.Controllers;

import com.costea.GreatestHits.DataObjects.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static com.costea.GreatestHits.Statics.Statics.*;
import static java.lang.Math.max;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class ChartsController {

    @GetMapping("/")
    public String getChartEntries(@RequestParam(value = "from",required = false) Integer from,
                                  @RequestParam(value = "to",required = false) Integer to,
                                  Model model){
        int size=chartSimulationProcess.getAllCharts().size();
        if(to==null)
            to=size;
        if(from==null)
            from=max(0,to-20);
        else from--; //off by one issues
        if(from>to) throw new ResponseStatusException(BAD_REQUEST);
        if(from<0) throw new ResponseStatusException(BAD_REQUEST);
        if(to>size) throw new ResponseStatusException(BAD_REQUEST);
        model.addAttribute("chart", chartSimulationProcess.getAllCharts().subList(from,to));
        model.addAttribute("from",from+1);
        model.addAttribute("to",to);
        model.addAttribute("size",size);
        return "index";
    }

    @GetMapping(value="/artist/{name}")
    public String getArtistByName(@PathVariable("name") String artistName,Model model)
    {
        List<SongListing> songList=new ArrayList<>();
        for(Artist artist : chartSimulationProcess.getArtists())
            if(Objects.equals(artist.getName(), artistName))
            {
                int artistId=artist.getID();
                for(Song song: chartSimulationProcess.getSongs())
                    if(song.getArtistID()==artistId)
                        if(song.getWeek()>0 && song.getPeak()<=nrChartEntries)
                           songList.add(new SongListing(song));
                songList.sort(Comparator.comparingInt(SongListing::getPositionAsInt));
                model.addAttribute("artistName",artist.getName());
                model.addAttribute("songList", songList);
                return "artist";
            }
        throw new ResponseStatusException(NOT_FOUND, "Artist not found!");
    }

    @GetMapping(value="/song/{artist}/{songName}")
    public String getSongByID(@PathVariable("artist") String artistName,
                              @PathVariable("songName") String songName,
                              Model model)
    {
        List<ChartEntry> chartEntries=new ArrayList<>();
        boolean foundEntries=false;
        for(Chart chart :chartSimulationProcess.getAllCharts())
            for (ChartEntry chartEntry : chart.getChartEntries())
                if (chartEntry.getArtistName().equals(artistName) && chartEntry.getSongName().equals(songName))
                {
                    foundEntries=true;
                    chartEntry.setCurrentWeek(chart.getWeek());
                    chartEntries.add(chartEntry);
                }
        if(foundEntries)
        {
            model.addAttribute("artistName",artistName);
            model.addAttribute("songName",songName);
            model.addAttribute("chartEntries", chartEntries);
            return "song";
        }
        else throw new ResponseStatusException(NOT_FOUND, "Song not found!");
    }

    @GetMapping(value="/data.json")
    @ResponseBody
    public ResponseEntity<String> readJSON() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartSimulationProcess.getAllCharts());
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.CREATED);
        }
        catch (JsonProcessingException ex)
        {
            throw new ResponseStatusException(NOT_FOUND);
        }

    }
}
