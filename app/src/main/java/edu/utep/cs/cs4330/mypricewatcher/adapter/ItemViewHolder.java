package edu.utep.cs.cs4330.mypricewatcher.adapter;

import android.content.SharedPreferences;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.utep.cs.cs4330.mypricewatcher.MainActivity;
import edu.utep.cs.cs4330.mypricewatcher.R;
import edu.utep.cs.cs4330.mypricewatcher.store.core.Store;
import edu.utep.cs.cs4330.mypricewatcher.store.core.StoreItem;
import edu.utep.cs.cs4330.mypricewatcher.store.core.StoreManager;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
    public StoreItem storeItem;
    public int position;
    public ItemAdapter parent;

    public ConstraintLayout mainLayout;

    public TextView textName;
    public TextView textInitialPrice;
    public TextView textCurrentPrice;
    public TextView textChangedPercent;
    public TextView textURL;

    public ImageView imageView;

    public Button btnUpdatePrice;
    public Button btnOpenURL;
    public Button btnModify;

    public ItemViewHolder(View itemView, ItemAdapter parent) {
        super(itemView);
        this.parent = parent;

        mainLayout = itemView.findViewById(R.id.item_main_layout);
        imageView = itemView.findViewById(R.id.imageView);
        textName = itemView.findViewById(R.id.itemTextName);
        textInitialPrice = itemView.findViewById(R.id.itemTextInitialPrice);
        textCurrentPrice = itemView.findViewById(R.id.itemTextCurrentPrice);
        textChangedPercent = itemView.findViewById(R.id.itemTextChangedPercent);
        textURL = itemView.findViewById(R.id.itemTextURL);

        itemView.setOnLongClickListener(this);
    }

    public void bindItem(StoreItem storeItem, int position) {
        this.storeItem = storeItem;
        this.position = position;
        updateView();
    }

    public void updateView() {
        textName.setText(storeItem.getFormattedName());
        textInitialPrice.setText(storeItem.getFormattedInitialPrice());
        textCurrentPrice.setText(storeItem.getFormattedCurrentPrice());
        textChangedPercent.setText(storeItem.getFormattedPercentChange());
        textURL.setText(storeItem.getFormattedURL());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
        if (prefs.getBoolean("storeIcons", true)) {
            imageView.setVisibility(View.VISIBLE);
            Store owner = StoreManager.getOwnerFromURL(storeItem.url);
            if (owner == null)
                imageView.setImageResource(R.drawable.ic_store_unknown);
            else
                imageView.setImageResource(owner.getBitmapResourceID());
        } else {
            imageView.setVisibility(View.GONE);
            return;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        // Notify parent listener
        parent.listener.onItemLongClicked(getAdapterPosition());
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        return true;
    }
}