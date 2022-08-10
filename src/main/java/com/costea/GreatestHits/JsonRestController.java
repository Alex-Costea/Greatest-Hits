package com.costea.GreatestHits;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.costea.GreatestHits.GreatestHitsApplication.*;

@RestController
public class JsonRestController {

    @GetMapping(value="/json")
    public String read() {
        try {
            var x= mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartSimulator.getAllCharts());
            System.out.println(x);
            return x;
        }
        catch (JsonProcessingException ex)
        {
            return "ERROR";
        }

    }
}
