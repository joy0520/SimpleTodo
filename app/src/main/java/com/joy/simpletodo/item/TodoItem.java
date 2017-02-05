package com.joy.simpletodo.item;

/**
 * Created by Joy on 2017/2/5.
 */

public class TodoItem {
    public int id;
    public String itemName;

    public TodoItem(int id, String itemName) {
        this.id = id;
        this.itemName = itemName;
    }

    @Override
    public String toString() {
        return "TodoItem(" + id + ", " + itemName + ")";
    }
}
