package com.joy.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joy.simpletodo.edit.EditItemActivity;
import com.joy.simpletodo.item.TodoItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 0;
    public static final String INTENT_EXTRA_ITEM_NAME = "itemName";
    public static final String INTENT_EXTRA_ITEM_DUE_DATE = "itemDueDate";

    public static final boolean DEBUG = true;

    private ArrayList<TodoItem> mItems;
    private ItemsAdapter mItemAdapter;
    private ListView mItemsListView;
    /**
     * Record the item's position which is about to be updated.
     */
    private int mItemToBeUpdated;

    private ItemsSQLiteHelper mItemsSQLHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mItemsListView = (ListView) findViewById(R.id.lvItems);
        mItemsSQLHelper = ItemsSQLiteHelper.getInstance(this);

        readItems();

        // Create the adapter for the list mItems.
        mItemAdapter = new ItemsAdapter(this, mItems);
        mItemsListView.setAdapter(mItemAdapter);
        setupListViewListeners();
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        // Prevent empty item.
        if (itemText.isEmpty()) {
            Toast.makeText(this, "Todo item cannot be empty", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        TodoItem item = new TodoItem(mItemAdapter.getCount(), itemText);
        // Assign a temporary due date.
        GregorianCalendar c = new GregorianCalendar();
        item.setDueDate(new GregorianCalendar(c.get(GregorianCalendar.YEAR),
                c.get(GregorianCalendar.MONTH),
                c.get(GregorianCalendar.DAY_OF_MONTH)));
        Log.i("joy.onAddItem", "" + item);

        mItemAdapter.add(item);
        mItemsListView.setSelection(mItemsListView.getCount() - 1); //scroll to bottom
        etNewItem.setText("");
        addItem(item);
    }

    public void setupListViewListeners() {
        mItemsListView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view, int position, long id) {
                        // Remove an item when long-clicked
                        deleteItem(mItems.get(position));
                        mItems.remove(position);
                        mItemAdapter.notifyDataSetChanged();
                        return true;
                    }
                });
        mItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                if (view instanceof ViewGroup) {
                    // Go to an edit page for the item clicked
                    mItemToBeUpdated = position;
                    ViewGroup layout = (ViewGroup) view;
                    String itemName = ((TextView) layout.findViewById(R.id.item_name))
                            .getText().toString();
                    Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                    intent.putExtra(INTENT_EXTRA_ITEM_NAME, itemName);
                    intent.putExtra(INTENT_EXTRA_ITEM_DUE_DATE,
                            mItemAdapter.getItem(position).dueDate.getTime());
                    MainActivity.this.startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            // Get the result and update the corresponding item
            String newItemName = data.getStringExtra(INTENT_EXTRA_ITEM_NAME);
            TodoItem item = new TodoItem(mItems.get(mItemToBeUpdated).id, newItemName);
            // Update due date
            Date date = (Date) data.getSerializableExtra(INTENT_EXTRA_ITEM_DUE_DATE);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            item.setDueDate(calendar);

            mItems.remove(mItemToBeUpdated);
            mItems.add(mItemToBeUpdated, item);
            mItemAdapter.notifyDataSetChanged();
            updateItem(item);
        }
    }

    /**
     * Read the list of items saved int a file todo.txt.
     */
    private void readItems() {
        mItems = new ArrayList<>();
        List<TodoItem> list = mItemsSQLHelper.getAllItems();
        for (TodoItem item : list) {
            mItems.add(item);
        }
    }

    private void addItem(TodoItem item) {
        mItemsSQLHelper.addItem(item);
    }

    private void updateItem(TodoItem item) {
        mItemsSQLHelper.updateItem(item);
    }

    private void deleteItem(TodoItem item) {
        mItemsSQLHelper.deleteItem(item);
    }
}
