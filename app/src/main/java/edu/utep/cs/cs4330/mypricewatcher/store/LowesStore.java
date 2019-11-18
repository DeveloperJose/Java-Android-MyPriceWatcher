package edu.utep.cs.cs4330.mypricewatcher.store;

import edu.utep.cs.cs4330.mypricewatcher.R;
import edu.utep.cs.cs4330.mypricewatcher.networking.NetworkUtil;
import edu.utep.cs.cs4330.mypricewatcher.store.core.WebStore;

public class LowesStore extends WebStore {
    @Override
    protected double getPriceFromHTML(String html) throws Exception {
        try {
            String price2 = NetworkUtil.getTagContent(html, "itemprop=\"PriceCurrency\" content=\"USD\">$</sup>", "</span>");
            return Double.parseDouble(price2);
        } catch (NumberFormatException ex) {
            String priceStr = NetworkUtil.getTagContent(html, "\"sellingPrice\":", ",");
            return Double.parseDouble(priceStr);
        }
    }

    @Override
    protected String getNameFromHTML(String html) throws Exception {
        return NetworkUtil.getTagContent(html, "<h1 class=\"h3\">", "</h1>");
    }

    @Override
    public int getBitmapResourceID() {
        return R.drawable.ic_store_lowes;
    }
}
