package com.costea.GreatestHits.Statics;

import com.costea.GreatestHits.Processes.ChartSimulationProcess;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Random;

public class Statics
{
    private Statics(){}

    public static final Random ran = new Random(); // random number generator
    public static final int nrChartEntries = 20; //chart entries to be shown every week
    public static int nextSongID =0; // which ID are we at?
    public static ChartSimulationProcess chartSimulationProcess;
    public static final ObjectMapper mapper = new ObjectMapper();
}
