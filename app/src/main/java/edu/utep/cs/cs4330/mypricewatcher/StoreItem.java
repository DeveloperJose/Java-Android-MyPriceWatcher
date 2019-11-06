package edu.utep.cs.cs4330.mypricewatcher;

public class StoreItem {
    public Store storeOwner;
    public String name;
    public String url;
    public double initialPrice;

    public StoreItem(Store storeOwner, String name, String url, double initialPrice){
        this.storeOwner = storeOwner;
        this.name = name;
        this.url = url;
        this.initialPrice = initialPrice;
    }

    public double getCurrentPrice(){
        return storeOwner.getPriceFromURL(url);
    }

    public static double calculatePercentChange(double initialPrice, double currentPrice){
        double change = Math.abs(currentPrice - initialPrice);
        double percent = change / initialPrice * 100;
        return percent;
    }
}
