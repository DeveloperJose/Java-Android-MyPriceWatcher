package edu.utep.cs.cs4330.mypricewatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ItemActivity extends AppCompatActivity {
    public static final String KEY_NAME = "ITEM_NAME";
    public static final String KEY_INIT_PRICE = "ITEM_INIT_PRICE";
    public static final String KEY_URL = "URL";

    public EditText editName;
    public EditText editInitialPrice;
    public EditText editURL;
    public Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        editName = findViewById(R.id.editName);
        editInitialPrice = findViewById(R.id.editInitialPrice);
        editURL = findViewById(R.id.editURL);
        btnSubmit = findViewById(R.id.btnSubmit);

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            if(extra.containsKey(KEY_URL)){
                String passedURL = extra.getString(KEY_URL);
                editURL.setText(passedURL);
            }

            if(extra.containsKey(KEY_NAME)){
                String passedName = extra.getString(KEY_NAME);
                editName.setText(passedName);
            }

            if(extra.containsKey(KEY_INIT_PRICE)) {
                double passedInitialPrice = extra.getDouble(KEY_INIT_PRICE);
                editInitialPrice.setText("" + passedInitialPrice);
            }
        }
    }

    public void onClickBtnSubmit(View view){
        if(editName.getText().toString().isEmpty()){
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        if(editInitialPrice.getText().toString().isEmpty()){
            Toast.makeText(this, "Initial price cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        if(editURL.getText().toString().isEmpty()){
            Toast.makeText(this, "URL cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_NAME, editName.getText().toString());
        resultIntent.putExtra(KEY_INIT_PRICE, editInitialPrice.getText().toString());
        resultIntent.putExtra(KEY_URL, editURL.getText().toString());

        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    }
}
