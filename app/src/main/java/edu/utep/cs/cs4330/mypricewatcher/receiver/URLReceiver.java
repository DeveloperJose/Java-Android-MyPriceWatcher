package edu.utep.cs.cs4330.mypricewatcher.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.browser.customtabs.CustomTabsIntent;

import edu.utep.cs.cs4330.mypricewatcher.ItemActivity;

public class URLReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String receivedURL = intent.getDataString();

        Intent myIntent = new Intent(context, ItemActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.putExtra(ItemActivity.KEY_URL, receivedURL);
        context.startActivity(myIntent);
    }
}
