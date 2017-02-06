package com.joy.simpletodo.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.joy.simpletodo.MainActivity;
import com.joy.simpletodo.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.R.attr.format;
import static com.joy.simpletodo.MainActivity.INTENT_EXTRA_ITEM_DUE_DATE;
import static com.joy.simpletodo.MainActivity.INTENT_EXTRA_ITEM_NAME;

/**
 * Created by Joy on 2017/2/3.
 */

public class EditItemActivity extends Activity implements View.OnClickListener {
    public static final String BUNDLE_EXTRA_DATE = "bundle_extra_date";

    private EditText mEditText;
    private Button mButton;
    private TextView mDueDateView;

    private String mOriginalItemName;
    private Date mDueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        mEditText = (EditText) findViewById(R.id.editText);
        mButton = (Button) findViewById(R.id.button);
        mDueDateView = (TextView) findViewById(R.id.date_spinner);
        mDueDateView.setOnClickListener(this);

        String itemName = getIntent().getStringExtra(INTENT_EXTRA_ITEM_NAME);
        mOriginalItemName = itemName;
        mEditText.setText(itemName);

        mDueDate = (Date) getIntent().getSerializableExtra(INTENT_EXTRA_ITEM_DUE_DATE);
        updateDueDateView(mDueDate);
    }

    public void onSaveItem(View v) {
        finish();
    }

    @Override
    public void finish() {
        String newItemName = mEditText.getText().toString();
        if (newItemName.isEmpty()) {
            Toast.makeText(this, "Todo item cannot be empty", Toast.LENGTH_SHORT)
                    .show();
            newItemName = mOriginalItemName;
        }
        Intent resultIntent = new Intent();
        resultIntent.putExtra(INTENT_EXTRA_ITEM_NAME, newItemName);
        resultIntent.putExtra(INTENT_EXTRA_ITEM_DUE_DATE, mDueDate);

        setResult(MainActivity.REQUEST_CODE, resultIntent);
        super.finish();
    }

    @Override
    public void onClick(View v) {
        if (v == mDueDateView) {
            showDatePickerDialog();
        }
    }

    public void showDatePickerDialog() {
        // Open the date picked dialog
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle bundle = new Bundle();

        bundle.putSerializable(BUNDLE_EXTRA_DATE, mDueDate);
        fragment.setArguments(bundle);

        fragment.show(getFragmentManager(), "datePicker");
    }

    // TODO should pass a serializable to handle more data like hour, minute, second, etc...
    public void onSetDueDateInDialog(int year, int month, int dayOfMonth) {
        // Update the due date from DatePickerFragment
        GregorianCalendar c = new GregorianCalendar(year, month, dayOfMonth);
        mDueDate = c.getTime();
        updateDueDateView(mDueDate);
    }

    private void updateDueDateView(Date date) {
        if (mDueDateView != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy, MMM d");
            String dateString = format.format(date);
            mDueDateView.setText(dateString);
        }
    }

}
