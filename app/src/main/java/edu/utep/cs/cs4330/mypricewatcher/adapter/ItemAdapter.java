package edu.utep.cs.cs4330.mypricewatcher.adapter;

import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.utep.cs.cs4330.mypricewatcher.R;
import edu.utep.cs.cs4330.mypricewatcher.store.core.StoreItem;

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    public List<StoreItem> itemList;
    public LongClickListener listener;
    public int activatedIndex = -1;

    public ItemAdapter(List<StoreItem> itemList, LongClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false);
        ItemViewHolder vh = new ItemViewHolder(itemView, this);
        return vh;

    }

    @Override
    public void onBindViewHolder(ItemViewHolder viewHolder, int position) {
        StoreItem item = itemList.get(position);
        viewHolder.bindItem(item, position);
        viewHolder.itemView.setActivated(position == activatedIndex);
        viewHolder.mainLayout.setOnLongClickListener((view) -> {
            listener.onItemLongClicked(position);
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public StoreItem getCurrentItem() {
        if (activatedIndex != -1)
            return itemList.get(activatedIndex);
        return null;
    }

    public interface LongClickListener {
        void onItemLongClicked(int position);
    }
}
