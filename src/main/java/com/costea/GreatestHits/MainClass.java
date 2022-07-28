package com.costea.GreatestHits;

import java.io.IOException;
import java.util.Arrays;

public class MainClass {
    public static void main(String[] args) {
        try {
            ChartSimulator chartSimulator = new ChartSimulator();
            for (int i = 1; i <= 52; i++)
                chartSimulator.nextWeek();
            chartSimulator.displayYearEnd();
            chartSimulator.closeWriter();
        }
        catch(IOException ex)
        {
            System.out.println("File reading/writing didn't work!");
            ex.printStackTrace();
        }
    }
}
