package com.joy.simpletodo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.joy.simpletodo.item.TodoItem;

import java.util.ArrayList;

/**
 * Adapter class to manage TodoItems.
 * Created by Joy on 2017/2/5.
 */

public class ItemsAdapter extends ArrayAdapter<TodoItem> {

    public ItemsAdapter(Context context, ArrayList<TodoItem> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the item for this position
        TodoItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.todo_item, parent, false);
        }
        // Lookup view for data population
        TextView itemNameView = (TextView) convertView.findViewById(R.id.item_name);
        // Populate the data into the template view using the data object
        itemNameView.setText(item.itemName);
        // Return the completed view to render on screen
        return convertView;

    }
}
