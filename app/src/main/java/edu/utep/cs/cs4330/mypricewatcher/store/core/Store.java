package edu.utep.cs.cs4330.mypricewatcher.store.core;

import java.net.MalformedURLException;

public abstract class Store {
    public abstract StoreItem getItemFromURL(String itemURL) throws MalformedURLException, Exception;

    public abstract int getBitmapResourceID();
}
