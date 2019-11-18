package edu.utep.cs.cs4330.mypricewatcher;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.android.material.snackbar.Snackbar;

import java.net.MalformedURLException;

import edu.utep.cs.cs4330.mypricewatcher.receiver.URLReceiver;
import edu.utep.cs.cs4330.mypricewatcher.store.SimulatedStore;
import edu.utep.cs.cs4330.mypricewatcher.store.core.Store;
import edu.utep.cs.cs4330.mypricewatcher.store.core.StoreItem;
import edu.utep.cs.cs4330.mypricewatcher.store.core.StoreManager;

public class ItemActivity extends AppCompatActivity {
    public static final String KEY_NAME = "ITEM_NAME";
    public static final String KEY_URL = "URL";
    public static final String KEY_INITIAL_PRICE = "INITIAL_PRICE";

    private Handler handlerUI;
    private ProgressBar progressBar;
    private EditText editName;
    private EditText editURL;
    private EditText editPrice;
    private Button btnAddItem;
    private Button btnFetchItem;
    private ImageButton btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        handlerUI = new Handler();

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        // Enable backward arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar = findViewById(R.id.progressBar);
        editName = findViewById(R.id.editName);
        editPrice = findViewById(R.id.editPrice);
        editURL = findViewById(R.id.editURL);
        btnAddItem = findViewById(R.id.btnAdd);
        btnFetchItem = findViewById(R.id.btnFetch);
        btnSearch = findViewById(R.id.btnSearch);

        progressBar.setVisibility(View.INVISIBLE);
        editPrice.setEnabled(false);
        editName.setEnabled(false);
        btnAddItem.setEnabled(false);

        if (savedInstanceState != null)
            populateWithBundle(savedInstanceState);
        else if (getIntent().getExtras() != null)
            populateWithBundle(getIntent().getExtras());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getExtras() != null)
            populateWithBundle(intent.getExtras());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    public void onClickBtnFetch(View view) {
        String itemURL = editURL.getText().toString();

        if (itemURL.isEmpty()) {
            Snackbar.make(view, "Cannot fetch details from an empty URL.", Snackbar.LENGTH_LONG).show();
            return;
        }

        // Attempt to get the item details from the store
        Store linkedStore = StoreManager.getOwnerFromURL(itemURL);
        if (linkedStore == null) {
            Snackbar.make(view, "This store is not supported by the app yet.", Snackbar.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        setEnabledTop(false);
        setEnabledBottom(false);

        new Thread(() -> {
            try {
                StoreItem item = linkedStore.getItemFromURL(itemURL);
                handlerUI.post(() -> {
                    updateItemUI(item);
                });
            } catch (MalformedURLException ex) {
                handlerUI.post(() -> {
                    showMessage("The URL is malformed/invalid.");
                });
            } catch (Exception ex) {
                handlerUI.post(() -> {
                    showMessage("Could not parse item details from HTML in page.");
                    Log.d("PriceWatch", "Exception: " + ex.getClass());
                    Log.d("PriceWatch", ex.toString());
                    Log.d("PriceWatch", ex.getMessage());
                });
            }
            handlerUI.post(() -> {
                setEnabledTop(true);
                progressBar.setVisibility(View.INVISIBLE);
            });
        }
        ).start();
    }

    private void showMessage(String message) {
        Snackbar.make(this.btnAddItem, message, Snackbar.LENGTH_LONG).show();
    }

    private void updateItemUI(StoreItem item) {
        editName.setText(item.name);
        editPrice.setText(String.format("$%.2f", item.initialPrice));
        setEnabledTop(true);
        setEnabledBottom(true);
    }

    public void onClickBtnAdd(View view) {
        String itemURL = editURL.getText().toString();
        String itemName = editName.getText().toString();

        if (itemURL.isEmpty()) {
            Snackbar.make(view, "URL cannot be empty.", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (itemName.isEmpty()) {
            Snackbar.make(view, "Name cannot be empty.", Snackbar.LENGTH_LONG).show();
            return;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_NAME, editName.getText().toString());
        resultIntent.putExtra(KEY_URL, editURL.getText().toString());
        resultIntent.putExtra(KEY_INITIAL_PRICE, editPrice.getText().toString());

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public void onClickBtnSearch(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);

        popupMenu.inflate(R.menu.search_menu);
        popupMenu.show();
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.search_menu_homedepot) {
            openChromeCustomTab("https://www.homedepot.com/");
        } else if (item.getItemId() == R.id.search_menu_lowes) {
            openChromeCustomTab("https://www.lowes.com/");
        } else if (item.getItemId() == R.id.search_menu_amazon) {
            openChromeCustomTab("https://www.amazon.com/");
        } else if (item.getItemId() == R.id.search_menu_simulated) {
            String generatedString = SimulatedStore.generateRandString(15);
            editURL.setText("|SIMULATED|" + generatedString);
        }
        return true;
    }

    private void populateWithBundle(Bundle b) {
        if (b != null) {
            if (b.containsKey(KEY_URL)) {
                String passedURL = b.getString(KEY_URL);
                editURL.setText(passedURL);
            }

            if (b.containsKey(KEY_NAME)) {
                String passedName = b.getString(KEY_NAME);
                editName.setText(passedName);
            }
        }
    }

    private void openChromeCustomTab(String homeURL) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        Intent intent = new Intent(this, URLReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Drawable drawable = getDrawable(R.drawable.ic_action_add);
        Bitmap icon = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(icon);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        builder.setActionButton(icon, "Add to watch list", pendingIntent);
        builder.addMenuItem("Add to watch list", pendingIntent);
        builder.setToolbarColor(Color.RED);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(homeURL));
    }

    private void setEnabledTop(boolean isEnabled) {
        editURL.setEnabled(isEnabled);
        btnSearch.setEnabled(isEnabled);
        btnFetchItem.setEnabled(isEnabled);
    }

    private void setEnabledBottom(boolean isEnabled) {
        editName.setEnabled(isEnabled);
        btnAddItem.setEnabled(isEnabled);
    }
}
