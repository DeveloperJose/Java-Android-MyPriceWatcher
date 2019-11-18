package edu.utep.cs.cs4330.mypricewatcher.store.core;

import android.text.Html;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class StoreItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="url")
    public String url;

    @ColumnInfo(name="name")
    public String name;

    @ColumnInfo(name="initial_price")
    public double initialPrice;

    @ColumnInfo(name="last_checked_price")
    public double lastCheckedPrice;

    public StoreItem(String url, String name, double initialPrice) {
        this(url, name, initialPrice, initialPrice);
    }

    public StoreItem(String url, String name, double initialPrice, double currentPrice) {
        this.name = name;
        this.url = url;
        this.initialPrice = initialPrice;
        this.lastCheckedPrice = currentPrice;
    }

    public static double calculatePercentChange(double initialPrice, double currentPrice) {
        double change = Math.abs(currentPrice - initialPrice);
        double percent = change / initialPrice * 100;
        return percent;
    }

    public Spanned getFormattedName() {
        return Html.fromHtml(name);
    }

    public Spanned getFormattedPercentChange() {
        String format = "";
        double changedPercent = calculatePercentChange(initialPrice, lastCheckedPrice);
        if (initialPrice == lastCheckedPrice) {
            format = "<font color=\"red\"><b>No Change</b></font>";
        } else if (initialPrice > lastCheckedPrice) {
            format += "<font color=\"green\"><b>";
            format += String.format("Decreased by %.3f", changedPercent);
            format += "%</b></font>";
        } else {
            format += "<font color=\"red\"><b>";
            format += String.format("Increased by %.3f", changedPercent);
            format += "%</b></font>";
        }
        return Html.fromHtml(format);
    }

    public Spanned getFormattedInitialPrice() {
        return Html.fromHtml(String.format("$%.2f", initialPrice));
    }

    public Spanned getFormattedCurrentPrice(){
        return Html.fromHtml(String.format("$%.2f", lastCheckedPrice));
    }

    public Spanned getFormattedURL(){
        return Html.fromHtml(url);
    }
}