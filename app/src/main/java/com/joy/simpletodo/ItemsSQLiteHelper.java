package com.joy.simpletodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.joy.simpletodo.item.TodoItem;

import java.util.ArrayList;
import java.util.List;

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
                + KEY_ITEM_ID + " INTEGER PRIMARY KEY," // primary key
                + KEY_ITEM_NAME + " TEXT" // item text
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
            values.put(KEY_ITEM_ID, item.id);
            values.put(KEY_ITEM_NAME, item.itemName);

            long result = db.insertOrThrow(TABLE_ITEMS, null, values);
            if (result == -1) {
                if (DEBUG) Log.d(TAG, "result == -1, failed to add item " + item.itemName);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
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
        int rows = db.update(TABLE_ITEMS, values,
                KEY_ITEM_ID + "= ?", new String[]{Integer.toString(item.id)});
        // Check if update succeeded
        if (rows != 1) {
            if (DEBUG) Log.d(TAG, "rows != 1, failed to update item " + item);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void deleteItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        int rows = db.delete(TABLE_ITEMS, KEY_ITEM_ID + "=" + id, null);
        if (DEBUG) Log.d(TAG, "Delete rows=" + rows);
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
                items.add(item);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return items;
    }
}
