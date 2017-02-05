package com.joy.simpletodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.joy.simpletodo.MainActivity.INTENT_EXTRA_ITEM_NAME;

/**
 * Created by Joy on 2017/2/3.
 */

public class EditItemActivity extends Activity {
    EditText mEditText;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        mEditText = (EditText) findViewById(R.id.editText);
        mButton = (Button) findViewById(R.id.button);

        String itemName = getIntent().getStringExtra(INTENT_EXTRA_ITEM_NAME);
        mEditText.setText(itemName);
    }

    public void onSaveItem(View v) {
        finish();
    }

    @Override
    public void finish() {
        String newItemName = mEditText.getText().toString();
        Intent resultIntent = new Intent();
        resultIntent.putExtra(INTENT_EXTRA_ITEM_NAME, newItemName);

        setResult(MainActivity.REQUEST_CODE, resultIntent);
        super.finish();
    }
}
