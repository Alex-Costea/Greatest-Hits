package com.costea.GreatestHits;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.costea.GreatestHits.GreatestHitsApplication.*;

@RestController
public class JsonRestController {

    @GetMapping(value="/data.json")
    public ResponseEntity<String> readJSON() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartSimulator.getAllCharts());
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.CREATED);
        }
        catch (JsonProcessingException ex)
        {
            return null;
        }

    }
}
