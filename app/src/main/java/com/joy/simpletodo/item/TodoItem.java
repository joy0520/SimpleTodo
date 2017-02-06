package com.joy.simpletodo.item;

import java.util.Calendar;

/**
 * Created by Joy on 2017/2/5.
 */

public class TodoItem {
    public int id;
    public String itemName;
    public Calendar dueDate;

    public TodoItem(int id, String itemName) {
        this.id = id;
        this.itemName = itemName;
    }

    public void setDueDate(Calendar date) {
        dueDate = date;
    }

    @Override
    public String toString() {
        return "TodoItem(" + id + ", " + itemName + ", " + dueDate + ")";
    }
}
