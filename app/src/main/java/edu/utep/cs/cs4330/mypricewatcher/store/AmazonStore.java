package edu.utep.cs.cs4330.mypricewatcher.store;

import edu.utep.cs.cs4330.mypricewatcher.R;
import edu.utep.cs.cs4330.mypricewatcher.networking.NetworkUtil;
import edu.utep.cs.cs4330.mypricewatcher.store.core.WebStore;

public class AmazonStore extends WebStore {
    @Override
    protected double getPriceFromHTML(String html) throws Exception {
        try {
            String priceStr = NetworkUtil.getTagContent(html, "priceBlockBuyingPriceString\">", "</span>");
            priceStr = priceStr.replace("$", "");
            return Double.parseDouble(priceStr);
        } catch (NumberFormatException ex) {
            try {
                String p2 = NetworkUtil.getTagContent(html, "<span id=\"price_inside_buybox\" class=\"a-size-medium a-color-price\">", "</span>");
                p2 = p2.replace("$", "");
                return Double.parseDouble(p2);
            } catch (NumberFormatException ex2) {
                String p2 = NetworkUtil.getTagContent(html, "priceBlockDealPriceString\">", "</span>");
                p2 = p2.replace("$", "");
                return Double.parseDouble(p2);
            }
        }
    }

    @Override
    protected String getNameFromHTML(String html) throws Exception {
        return "In The Future";
    }

    @Override
    public int getBitmapResourceID() {
        return R.drawable.ic_store_amazon;
    }
}
