package com.joy.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.joy.simpletodo.item.TodoItem;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 0;
    public static final String INTENT_EXTRA_ITEM_NAME = "itemName";
    public static final String FILE_NAME = "todo.txt";

    private ArrayList<TodoItem> mItems;
    private ArrayList<String> mTempItemNames;
    private ItemsAdapter mItemAdapter;
    private ListView mItemsListView;
    /**
     * Record the item's position which is about to be updated.
     */
    private int mItemToBeUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mItemsListView = (ListView) findViewById(R.id.lvItems);
        readItems();

        // Create the adapter for the list mItems.
        mItemAdapter = new ItemsAdapter(this, mItems);
        mItemsListView.setAdapter(mItemAdapter);
        setupListViewListeners();
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        TodoItem item = new TodoItem(itemText);
        mItemAdapter.add(item);
        mItemsListView.setSelection(mItemsListView.getCount() - 1); //scroll to bottom
        etNewItem.setText("");
        writeItems();
    }

    public void setupListViewListeners() {
        mItemsListView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view, int position, long id) {
                        // Remove an item when long-clicked
                        mItems.remove(position);
                        mItemAdapter.notifyDataSetChanged();
                        writeItems();
                        return true;
                    }
                });
        mItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                if (view instanceof LinearLayout) {
                    // Go to an edit page for the item clicked
                    mItemToBeUpdated = position;
                    LinearLayout layout = (LinearLayout) view;
                    String itemName = ((TextView) layout.findViewById(R.id.item_name))
                            .getText().toString();
                    Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                    intent.putExtra(INTENT_EXTRA_ITEM_NAME, itemName);
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
            mItems.remove(mItemToBeUpdated);
            mItems.add(mItemToBeUpdated, new TodoItem(newItemName));
            mItemAdapter.notifyDataSetChanged();
            writeItems();
        }
    }

    /**
     * Read the list of items saved int a file todo.txt.
     */
    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, FILE_NAME);
        try {
            mItems = new ArrayList<>();
            mTempItemNames = new ArrayList<String>(FileUtils.readLines(todoFile));
            for (String itemName : mTempItemNames) {
                mItems.add(new TodoItem(itemName));
            }
        } catch (IOException e) {
            mItems = new ArrayList<>();
        }
    }

    /**
     * Write the list of items saved int a file todo.txt.
     */
    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, FILE_NAME);
        try {
            mTempItemNames.clear();
            for (TodoItem item : mItems) {
                mTempItemNames.add(item.itemName);
            }
            FileUtils.writeLines(todoFile, mTempItemNames);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
