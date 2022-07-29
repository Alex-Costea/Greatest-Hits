package com.costea.GreatestHits;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.costea.GreatestHits.MainClass.chartSimulator;

@Controller
@RequestMapping("/GreatestHits")
public class ChartEntryController {

    @GetMapping()
    public String getAllChartEntries(Model model){
        model.addAttribute("entries", chartSimulator.getCurrentChart().getChartEntries());
        return "GreatestHits";
    }
}
