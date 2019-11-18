package edu.utep.cs.cs4330.mypricewatcher.store.core;

import java.net.MalformedURLException;

import edu.utep.cs.cs4330.mypricewatcher.networking.Network;

public abstract class WebStore extends Store {
    protected abstract double getPriceFromHTML(String html) throws Exception;

    protected abstract String getNameFromHTML(String html) throws Exception;

    @Override
    public StoreItem getItemFromURL(String itemURL) throws MalformedURLException, Exception {
        String html = Network.getHTML(itemURL);
        double price = getPriceFromHTML(html);
        String name = "";
        try {
            name = getNameFromHTML(html);
        } catch (Exception ex) {

        }

        return new StoreItem(itemURL, name, price);
    }


}
