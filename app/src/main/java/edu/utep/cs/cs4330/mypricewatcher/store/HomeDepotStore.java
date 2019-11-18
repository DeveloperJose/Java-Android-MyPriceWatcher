package edu.utep.cs.cs4330.mypricewatcher.store;

import edu.utep.cs.cs4330.mypricewatcher.R;
import edu.utep.cs.cs4330.mypricewatcher.networking.Network;
import edu.utep.cs.cs4330.mypricewatcher.networking.NetworkUtil;
import edu.utep.cs.cs4330.mypricewatcher.store.core.WebStore;

public class HomeDepotStore extends WebStore {
    @Override
    protected double getPriceFromHTML(String html) throws Exception {
        html = Network.clean(html);
        try {
            String dollarStr = NetworkUtil.getTagContent(html, "<span class=\"price__dollars\">", "</span>");
            String centStr = NetworkUtil.getTagContent(html, "<span class=\"price__cents\">", "</span>");
            double dollar = Double.parseDouble(dollarStr);
            double cent = Double.parseDouble(centStr) / 100d;
            return dollar + cent;
        } catch (NumberFormatException ex) {
            String att2 = NetworkUtil.getTagContent(html, "<span id=\"ajaxPriceAlt\" class=\"pReg\" itemprop=\"price\">", "</span>");
            att2 = att2.replace("$", "");
            return Double.parseDouble(att2);
        }
    }

    @Override
    protected String getNameFromHTML(String html) throws Exception {
        return NetworkUtil.getTagContent(html, "<h1 class=\"product-title__title\">", "</h1>");
    }

    @Override
    public int getBitmapResourceID() {
        return R.drawable.ic_store_homedepot;
    }
}
