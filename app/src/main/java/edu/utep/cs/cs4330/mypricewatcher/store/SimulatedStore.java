package edu.utep.cs.cs4330.mypricewatcher.store;

import java.net.MalformedURLException;
import java.util.Random;

import edu.utep.cs.cs4330.mypricewatcher.R;
import edu.utep.cs.cs4330.mypricewatcher.store.core.Store;
import edu.utep.cs.cs4330.mypricewatcher.store.core.StoreItem;

public class SimulatedStore extends Store {
    @Override
    public StoreItem getItemFromURL(String itemURL) throws MalformedURLException, Exception {
        Random rand = new Random(System.currentTimeMillis());
        double rangeMin = 5;
        double rangeMax = 100;
        double value = rangeMin + (rangeMax - rangeMin) * rand.nextDouble();
        return new StoreItem(itemURL, "Simulated Item", value);
    }

    public static String generateRandString(int targetStringLength) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    @Override
    public int getBitmapResourceID() {
        return R.drawable.ic_store_simulated;
    }
}
