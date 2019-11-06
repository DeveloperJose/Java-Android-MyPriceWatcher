package edu.utep.cs.cs4330.mypricewatcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;

    private List<StoreItem> itemList;
    private Store simulatedStore;

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recylerView);

        itemList = new ArrayList<>();
        simulatedStore = new SimulatedStore();

        // Example Product
        itemList.add(new StoreItem(simulatedStore, "Example1", "https://www.amazon.com/Neewer-Professional-Broadcasting-Microphone-Adjustable/dp/B00XOXRTX6/", 50));
        itemList.add(new StoreItem(simulatedStore, "Ex2", "http://www.utep.edu", 10));
        itemList.add(new StoreItem(simulatedStore, "Ex3", "http://www.utep.edu", 20));
        itemList.add(new StoreItem(simulatedStore, "Ex4", "http://www.utep.edu", 30));
        itemList.add(new StoreItem(simulatedStore, "Ex5", "http://www.utep.edu", 1));
        itemList.add(new StoreItem(simulatedStore, "Ex6", "http://www.utep.edu", 5));
        itemList.add(new StoreItem(simulatedStore, "Ex7", "http://www.utep.edu", 300));

        String action = getIntent().getAction();
        String type = getIntent().getType();
        if (Intent.ACTION_SEND.equalsIgnoreCase(action) && type != null && ("text/plain".equals(type))) {
            String url = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            Intent intent = new Intent(this, ItemActivity.class);
            intent.putExtra(ItemActivity.KEY_URL, url);
            startActivityForResult(intent, REQUEST_CODE);
        }

        recyclerView.setAdapter(new ItemAdapter(itemList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
    }

    public void onClickBtnAdd(View view){
        Intent intent = new Intent(this, ItemActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void onClickBtnSearch(View view){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addMenuItem("Add to watch list", pendingIntent);
        builder.setToolbarColor(Color.RED);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse("http://amazon.com/"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            String name = data.getStringExtra(ItemActivity.KEY_NAME);
            String initPriceStr = data.getStringExtra(ItemActivity.KEY_INIT_PRICE);
            double initPrice = Double.parseDouble(initPriceStr);
            String url = data.getStringExtra(ItemActivity.KEY_URL);

            StoreItem storeItem = new StoreItem(simulatedStore, name, url, initPrice);
            itemList.add(storeItem);
            recyclerView.getAdapter().notifyItemInserted(itemList.size());
        }
    }
}
