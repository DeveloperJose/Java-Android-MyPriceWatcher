package edu.utep.cs.cs4330.mypricewatcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import edu.utep.cs.cs4330.mypricewatcher.database.AppDatabase;
import edu.utep.cs.cs4330.mypricewatcher.adapter.ItemAdapter;
import edu.utep.cs.cs4330.mypricewatcher.receiver.WifiReceiver;
import edu.utep.cs.cs4330.mypricewatcher.store.core.Store;
import edu.utep.cs.cs4330.mypricewatcher.store.core.StoreItem;
import edu.utep.cs.cs4330.mypricewatcher.store.core.StoreManager;

public class MainActivity extends AppCompatActivity implements ItemAdapter.LongClickListener {
    public static final int REQUEST_CODE = 1;

    private List<StoreItem> itemList;

    private Handler handlerUI;
    private ProgressBar progressBar;
    private TextView textEmpty;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;

    private ActionModeCallback actionModeCallback;
    private WifiReceiver wifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiReceiver = new WifiReceiver();
        handlerUI = new Handler();
        itemList = new ArrayList<>();
        actionModeCallback = new ActionModeCallback(this);

        progressBar = findViewById(R.id.progressBar);
        textEmpty = findViewById(R.id.textEmpty);
        recyclerView = findViewById(R.id.recylerView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new ItemAdapter(itemList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        // Receive browser URL from Share button
        String action = getIntent().getAction();
        String type = getIntent().getType();
        if (Intent.ACTION_SEND.equalsIgnoreCase(action) && type != null && ("text/plain".equals(type))) {
            String url = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            Intent intent = new Intent(this, ItemActivity.class);
            intent.putExtra(ItemActivity.KEY_URL, url);
            startActivityForResult(intent, REQUEST_CODE);
        }

        updateList();

        new Thread(() -> {
            List<StoreItem> storedItems = AppDatabase.getInstance(this).storeItemDao().getAll();
            itemList.addAll(storedItems);
            adapter.notifyDataSetChanged();

            handlerUI.post(() -> {
                textEmpty.setText("No watched items in list (yet!)");
                progressBar.setVisibility(View.GONE);
                updateList();
            });
        }).start();

        // Register broadcast receiver for WIFI
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        registerReceiver(wifiReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_menu_add) {
            Intent intent = new Intent(this, ItemActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        } else if (item.getItemId() == R.id.main_menu_refresh_all) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(itemList.size());
            progressBar.setProgress(0);
            showMessage("Refreshing entire watch list...");
            new Thread(() -> {
                for (StoreItem storeItem : itemList) {
                    Store owner = StoreManager.getOwnerFromURL(storeItem.url);
                    try {
                        StoreItem newItem = owner.getItemFromURL(storeItem.url);
                        storeItem.lastCheckedPrice = newItem.initialPrice;
                        AppDatabase.getInstance(this).storeItemDao().update(storeItem);
                    } catch (MalformedURLException ex) {

                    } catch (Exception ex) {

                    }
                    handlerUI.post(() -> {
                        progressBar.setProgress(progressBar.getProgress() + 1);
                        adapter.notifyDataSetChanged();
                    });
                }
                handlerUI.post(() -> {
                    showMessage("Finished refreshing entire list.");
                    progressBar.setVisibility(View.GONE);
                });
            }).start();
        } else if (item.getItemId() == R.id.main_menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String url = data.getStringExtra(ItemActivity.KEY_URL);
            String name = data.getStringExtra(ItemActivity.KEY_NAME);
            String initPriceStr = data.getStringExtra(ItemActivity.KEY_INITIAL_PRICE);
            initPriceStr = initPriceStr.replace("$", "");

            double initPrice = Double.parseDouble(initPriceStr);
            StoreItem storeItem = new StoreItem(url, name, initPrice);
            itemList.add(storeItem);
            recyclerView.getAdapter().notifyItemInserted(itemList.size());
            updateList();

            new Thread(() -> {
                AppDatabase.getInstance(this).storeItemDao().insert(storeItem);
            }).start();
        }
    }

    private void updateList() {
        if (itemList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.GONE);
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        private Context context;

        public ActionModeCallback(Context context) {
            this.context = context;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.item_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            StoreItem storeItem = adapter.getCurrentItem();
            if (storeItem == null)
                return true;

            if (item.getItemId() == R.id.item_menu_edit) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.dialog_edit, null);
                EditText editName = dialogView.findViewById(R.id.editName);
                EditText editURL = dialogView.findViewById(R.id.editURL);

                editName.setText(storeItem.name);
                editURL.setText(storeItem.url);

                builder.setView(dialogView).setPositiveButton("Update Item", (dialogInterface, i) -> {
                            storeItem.name = editName.getText().toString();
                            storeItem.url = editURL.getText().toString();
                            adapter.notifyDataSetChanged();
                            new Thread(() -> {
                                AppDatabase.getInstance(context).storeItemDao().insertOrUpdate(storeItem);
                            }).start();
                        }
                ).setNegativeButton("Cancel", (dialogInterface, i) -> {
                });
                builder.create().show();
            } else if (item.getItemId() == R.id.item_menu_delete) {
                itemList.remove(adapter.activatedIndex);
                adapter.notifyDataSetChanged();
                new Thread(() -> {
                    AppDatabase.getInstance(context).storeItemDao().delete(storeItem);
                }).start();
            } else if (item.getItemId() == R.id.item_menu_browser) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(storeItem.url));
                startActivity(intent);
            } else if (item.getItemId() == R.id.item_menu_refresh) {
                Store linkedStore = StoreManager.getOwnerFromURL(storeItem.url);
                adapter.notifyDataSetChanged();
                showMessage("Refreshing item details in background...");
                new Thread(() -> {
                    try {
                        StoreItem newItem = linkedStore.getItemFromURL(storeItem.url);
                        storeItem.lastCheckedPrice = newItem.initialPrice;
                        Thread.sleep(3000);
                        handlerUI.post(() -> {
                            showMessage("Successfully refreshed watched item.");
                        });
                    } catch (MalformedURLException ex) {
                        handlerUI.post(() -> {
                            showMessage("The URL is malformed/invalid. Could not refresh item.");
                        });
                    } catch (Exception ex) {
                        handlerUI.post(() -> {
                            showMessage("Could not parse item details from HTML in page.");
                            Log.d("PriceWatch", "Exception: " + ex.getMessage());
                        });
                    }
                }
                ).start();

                adapter.notifyDataSetChanged();
            }
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // Update BG back to normal
            adapter.activatedIndex = -1;
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemLongClicked(int position) {
        // Update BG color and index
        adapter.activatedIndex = position;
        adapter.notifyDataSetChanged();
        startSupportActionMode(actionModeCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.notifyDataSetChanged();
    }


    private void showMessage(String message) {
        Snackbar.make(this.recyclerView, message, Snackbar.LENGTH_LONG).show();
    }
}
