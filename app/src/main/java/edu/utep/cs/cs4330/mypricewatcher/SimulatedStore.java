package edu.utep.cs.cs4330.mypricewatcher;

import java.util.Random;

public class SimulatedStore extends Store {

    @Override
    public double getPriceFromURL(String itemURL) {
        Random rand = new Random(System.currentTimeMillis());
        double rangeMin = 5;
        double rangeMax = 100;
        double value =  rangeMin + (rangeMax - rangeMin) * rand.nextDouble();

        return value;
    }
}
