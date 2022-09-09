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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static com.costea.GreatestHits.ChartSimulator.nrChartEntries;
import static com.costea.GreatestHits.GreatestHitsApplication.chartSimulator;
import static com.costea.GreatestHits.GreatestHitsApplication.mapper;
import static java.lang.Math.max;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class ChartsController {

    @GetMapping("/")
    public String getChartEntries(@RequestParam(value = "from",required = false) Integer from,
                                  @RequestParam(value = "to",required = false) Integer to,
                                  Model model){
        int size=chartSimulator.getAllCharts().size();
        if(to==null)
            to=size;
        if(from==null)
            from=max(0,to-20);
        else from--; //off by one issues
        if(from>to) throw new ResponseStatusException(BAD_REQUEST);
        if(from<0) throw new ResponseStatusException(BAD_REQUEST);
        if(to>size) throw new ResponseStatusException(BAD_REQUEST);
        model.addAttribute("chart", chartSimulator.getAllCharts().subList(from,to));
        model.addAttribute("from",from+1);
        model.addAttribute("to",to);
        model.addAttribute("size",size);
        return "index";
    }

    @GetMapping(value="/artist/{name}")
    public String getArtistByName(@PathVariable("name") String artistName,Model model)
    {
        List<SongListing> songList=new ArrayList<>();
        for(Artist artist : chartSimulator.getArtists())
            if(Objects.equals(artist.getName(), artistName))
            {
                int artistId=artist.getID();
                for(Song song: chartSimulator.getSongs())
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

    //TODO: Solve in case multiple songs with same artist and song title
    //TODO: Also make an actual page for it
    @GetMapping(value="/song/{artist}/{songName}")
    public String getSongByID(@PathVariable("artist") String artistName,
                              @PathVariable("songName") String songName,
                              Model model)
    {
        List<ChartEntry> chartEntries=new ArrayList<>();
        boolean foundEntries=false;
        for(Chart chart :chartSimulator.getAllCharts())
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
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartSimulator.getAllCharts());
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.CREATED);
        }
        catch (JsonProcessingException ex)
        {
            throw new ResponseStatusException(NOT_FOUND);
        }

    }
}
