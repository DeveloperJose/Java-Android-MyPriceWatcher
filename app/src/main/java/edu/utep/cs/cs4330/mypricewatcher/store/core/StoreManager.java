package edu.utep.cs.cs4330.mypricewatcher.store.core;

import edu.utep.cs.cs4330.mypricewatcher.store.AmazonStore;
import edu.utep.cs.cs4330.mypricewatcher.store.HomeDepotStore;
import edu.utep.cs.cs4330.mypricewatcher.store.LowesStore;
import edu.utep.cs.cs4330.mypricewatcher.store.SimulatedStore;

public class StoreManager {
    public static Store getOwnerFromURL(String itemURL) {
        if (itemURL.contains("|SIMULATED|"))
            return getSimulatedInstance();
        else if (itemURL.contains("homedepot.com"))
            return getHomeDepotInstance();
        else if (itemURL.contains("lowes.com"))
            return getLowesStoreInstance();
        else if (itemURL.contains("amazon.com"))
            return getAmazonStoreInstance();
        return null;
    }

    private static SimulatedStore simulatedStore;
    private static HomeDepotStore homeDepotStore;
    private static LowesStore lowesStore;
    private static AmazonStore amazonStore;

    private static SimulatedStore getSimulatedInstance() {
        if (simulatedStore == null)
            simulatedStore = new SimulatedStore();
        return simulatedStore;
    }

    private static HomeDepotStore getHomeDepotInstance() {
        if (homeDepotStore == null)
            homeDepotStore = new HomeDepotStore();
        return homeDepotStore;
    }

    private static LowesStore getLowesStoreInstance() {
        if (lowesStore == null)
            lowesStore = new LowesStore();
        return lowesStore;
    }

    private static AmazonStore getAmazonStoreInstance() {
        if (amazonStore == null)
            amazonStore = new AmazonStore();
        return amazonStore;
    }
}
