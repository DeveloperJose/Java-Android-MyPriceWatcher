package edu.utep.cs.cs4330.mypricewatcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {
    public List<StoreItem> itemList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public StoreItem storeItem;
        public int position;
        public ItemAdapter parent;

        public TextView textName;
        public TextView textInitialPrice;
        public TextView textCurrentPrice;
        public TextView textChangedPercent;
        public TextView textURL;

        public Button btnUpdatePrice;
        public Button btnOpenURL;
        public Button btnModify;

        public MyViewHolder(View itemView, ItemAdapter parent) {
            super(itemView);
            this.parent = parent;

            textName = itemView.findViewById(R.id.itemTextName);
            textInitialPrice = itemView.findViewById(R.id.itemTextInitialPrice);
            textCurrentPrice = itemView.findViewById(R.id.itemTextCurrentPrice);
            textChangedPercent = itemView.findViewById(R.id.itemTextChangedPercent);
            textURL = itemView.findViewById(R.id.itemTextURL);
            btnUpdatePrice = itemView.findViewById(R.id.itemBtnUpdatePrice);
            btnOpenURL = itemView.findViewById(R.id.itemBtnOpenURL);
            btnModify = itemView.findViewById(R.id.itemBtnModify);

            btnUpdatePrice.setOnClickListener(this::onClickBtnUpdate);
            btnOpenURL.setOnClickListener(this::onClickBtnOpenURL);
            btnModify.setOnClickListener(this::onClickBtnModify);
        }

        public void onClickBtnModify(View view){
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), btnModify);
            popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);

            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.modify_menu, popupMenu.getMenu());
            popupMenu.show();
        }

        public boolean onMenuItemClick(MenuItem item){
            switch(item.getItemId()){
                case R.id.modify_menu_edit:
                    Log.d("DevJ", "Manually edit");
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
                    View dialogView = inflater.inflate(R.layout.dialog_edit, null);
                    EditText editName = dialogView.findViewById(R.id.editName);
                    EditText editURL = dialogView.findViewById(R.id.editURL);

                    editName.setText(storeItem.name);
                    editURL.setText(storeItem.url);

                    builder.setView(dialogView).setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            storeItem.name = editName.getText().toString();
                            storeItem.url = editURL.getText().toString();
                            updateView();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.create().show();
                    return true;
                case R.id.modify_menu_remove:
                    parent.itemList.remove(position);
                    parent.notifyItemRemoved(position);
                    parent.notifyItemRangeChanged(position, parent.itemList.size());
                    return true;
            }
            return false;
        }

        public void onClickBtnUpdate(View view){
            updateView();
        }

        public void onClickBtnOpenURL(View view){
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(storeItem.url));
            itemView.getContext().startActivity(i);
        }

        public void bindItem(StoreItem storeItem, int position){
            this.storeItem = storeItem;
            this.position = position;
            updateView();
        }

        public void updateView(){
            double currentPrice = storeItem.getCurrentPrice();
            double changedPercent = StoreItem.calculatePercentChange(storeItem.initialPrice, currentPrice);
            String incOrDec = currentPrice > storeItem.initialPrice ? "Increased" : "Decreased";

            textName.setText(Html.fromHtml(storeItem.name));
            textInitialPrice.setText(String.format("$%.2f", storeItem.initialPrice));
            textCurrentPrice.setText(String.format("$%.2f", currentPrice));
            textChangedPercent.setText(String.format("%s by %.3f%%", incOrDec, changedPercent));
            textURL.setText(storeItem.url);
        }
    }

    public ItemAdapter(List<StoreItem> itemList) {
        this.itemList = itemList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item, parent, false);
        MyViewHolder vh = new MyViewHolder(itemView, this);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        StoreItem item = itemList.get(position);
        viewHolder.bindItem(item, position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
