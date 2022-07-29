package com.costea.GreatestHits;

import java.util.ArrayList;

public class Chart {
    private int week=-1;
    private ArrayList<ChartEntry> chartEntries=null;

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public ArrayList<ChartEntry> getChartEntries() {
        return chartEntries;
    }

    public void setChartEntries(ArrayList<ChartEntry> chartEntries) {
        this.chartEntries = chartEntries;
    }

    public Chart(int week, ArrayList<ChartEntry> chartEntries) {
        this.week=week;
        this.chartEntries=chartEntries;
    }

    public Chart()
    {

    }
}
