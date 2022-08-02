package com.costea.GreatestHits;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

public class Chart {
    private int week=-1;
    private ArrayList<ChartEntry> chartEntries=null;

    private LocalDate localDate=LocalDate.now();

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    @JsonIgnore
    public String getDateFormatted()
    {
        return localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
    }

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
