package com.joy.simpletodo;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.joy.simpletodo.item.TodoItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
        TextView itemNameView = (TextView) convertView.findViewById(R.id.item_name);
        itemNameView.setText(item.itemName);

        if (getItem(position).dueDate != null) {
            TextView itemDueDate = (TextView) convertView.findViewById(R.id.due_date);
            SimpleDateFormat format = new SimpleDateFormat("yyyy, MMM d");
            String date = format.format(getItem(position).dueDate.getTime());
            itemDueDate.setText(date);

            // Give the date text view color depending on if it's over or closing the due date
            GregorianCalendar c = new GregorianCalendar();
            GregorianCalendar calendar = new GregorianCalendar(c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            int compare = getItem(position).dueDate.compareTo(calendar);
            if (compare < 0) {
                itemDueDate.setTextColor(Color.RED);
            } else if (compare == 0) {
                itemDueDate.setTextColor(Color.MAGENTA);
            } else {
                itemDueDate.setTextColor(Color.GREEN);
            }
        }
        // Return the completed view to render on screen
        return convertView;

    }
}
