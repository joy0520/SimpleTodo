package com.joy.simpletodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.joy.simpletodo.item.TodoItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by Joy on 2017/2/5.
 */

public class ItemsSQLiteHelper extends SQLiteOpenHelper {
    private static final boolean DEBUG = MainActivity.DEBUG;

    private static final String TAG = "ItemsSQLiteHelper";
    // Database info
    private static final String DATABASE_NAME = "todoItems";
    // Table name
    private static final String TABLE_ITEMS = "items";
    // Items columns
    private static final String KEY_ITEM_ID = "id";
    private static final String KEY_ITEM_NAME = "itemName";
    private static final String KEY_ITEM_DUE_DATE = "dueDate";

    private static final int DATABASE_VERSION = 1;

    private static ItemsSQLiteHelper sInstance;

    /**
     * For singleton.
     */
    public static synchronized ItemsSQLiteHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ItemsSQLiteHelper(context);
        }
        return sInstance;
    }

    private ItemsSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + KEY_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" // primary key
                + "," + KEY_ITEM_NAME + " TEXT" // item text
                + "," + KEY_ITEM_DUE_DATE + " INTEGER" // item due date
                + ")";
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
            onCreate(db);
        }
    }

    public void addItem(TodoItem item) {
        if (item == null) return;

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ITEM_NAME, item.itemName);
            values.put(KEY_ITEM_DUE_DATE, item.dueDate.getTimeInMillis());

            long result = db.insertOrThrow(TABLE_ITEMS, null, values);
            if (result == -1) {
                if (DEBUG) Log.d(TAG, "result == -1, failed to add item " + item);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.d(TAG, "Failed to add item " + item, e);
        } finally {
            db.endTransaction();
        }
    }

    public void updateItem(TodoItem item) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_ID, item.id);
        values.put(KEY_ITEM_NAME, item.itemName);
        values.put(KEY_ITEM_DUE_DATE, item.dueDate.getTimeInMillis());
        int rows = db.update(TABLE_ITEMS, values,
                KEY_ITEM_ID + "= ?", new String[]{Integer.toString(item.id)});
        // Check if update succeeded
        if (rows != 1) {
            if (DEBUG) Log.d(TAG, "updateItem(): rows = "+rows+", failed to update item " + item);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void deleteItem(TodoItem item) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        int rows = db.delete(TABLE_ITEMS, KEY_ITEM_ID + "=" + item.id, null);
        if (DEBUG) Log.d(TAG, "Deleted " + item + " rows=" + rows);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<TodoItem> getAllItems() {
        List<TodoItem> items = new ArrayList<>();

        String SELECT_ITEMS_QUERY = String.format(
                "SELECT * FROM %s ",
                TABLE_ITEMS);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_ITEMS_QUERY, null);


        if (cursor.moveToFirst()) {
            do {
                String id_string = cursor.getString(cursor.getColumnIndexOrThrow(KEY_ITEM_ID));
                int id = Integer.valueOf(id_string);
                String name = cursor.getString(cursor.getColumnIndex(KEY_ITEM_NAME));
                TodoItem item = new TodoItem(id, name);
                // Set calendar for item
                long dueDate = cursor.getLong(cursor.getColumnIndex(KEY_ITEM_DUE_DATE));
                Date date = new Date(dueDate);
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                item.setDueDate(calendar);

                if (DEBUG) Log.d(TAG, "get all " + item);
                items.add(item);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return items;
    }
}
