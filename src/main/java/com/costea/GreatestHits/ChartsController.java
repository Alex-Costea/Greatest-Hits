package com.costea.GreatestHits;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.costea.GreatestHits.GreatestHitsApplication.chartSimulator;

@Controller
@RequestMapping("/")
public class ChartsController {

    @GetMapping()
    public String getChartEntries(Model model){
        model.addAttribute("chart", chartSimulator.getAllCharts());
        return "index";
    }
}
