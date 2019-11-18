package edu.utep.cs.cs4330.mypricewatcher.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import edu.utep.cs.cs4330.mypricewatcher.R;

public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                Toast.makeText(context, "Re-connected to WIFI! Thank you for following the instructions.", Toast.LENGTH_LONG).show();
            } else {
                // WiFI connection lost
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.dialog_wifi, null);

                builder.setView(dialogView).setPositiveButton("Open WiFi Settings", ((dialogInterface, i) ->
                {
                    context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                })).setNegativeButton("Ignore Warning", ((dialogInterface, i) -> {

                }));
                builder.create().show();
            }
        }
    }
}
