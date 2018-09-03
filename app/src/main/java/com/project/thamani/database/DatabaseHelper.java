package com.project.thamani.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.project.thamani.model.Note;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ravi on 15/03/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "notes_db";
    private static final String TAG = DatabaseHelper.class.getSimpleName();


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Note.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertNote(String uuid, String item, String price, String gtin,String warehouse,String wid,String manufacturer,String mid,String gs1,String retailer) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Note.COLUMN_UUID, uuid);
        values.put(Note.COLUMN_ITEM, item);
        values.put(Note.COLUMN_PRICE, price);
        values.put(Note.COLUMN_GTIN, gtin);
        values.put(Note.COLUMN_WARE, warehouse);
        values.put(Note.COLUMN_WID, wid);
        values.put(Note.COLUMN_MAN, manufacturer);
        values.put(Note.COLUMN_MID, mid);
        values.put(Note.COLUMN_GS1, gs1);
        values.put(Note.COLUMN_GS1, gs1);
        values.put(Note.COLUMN_RETAILER, retailer);

        // insert row
        long id = db.insert(Note.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Note getNote(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Note.TABLE_NAME,
                new String[]{Note.COLUMN_ID, Note.COLUMN_UUID, Note.COLUMN_ITEM, Note.COLUMN_PRICE, Note.COLUMN_GTIN, Note.COLUMN_WARE, Note.COLUMN_WID, Note.COLUMN_MAN, Note.COLUMN_MID, Note.COLUMN_GS1,Note.COLUMN_RETAILER, Note.COLUMN_TIMESTAMP},
                Note.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        // prepare note object
        Note note = new Note(
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_UUID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_ITEM)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_PRICE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_GTIN)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_MAN)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_MID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_WARE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_WID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_GS1)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_RETAILER)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return note;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Note.TABLE_NAME + " ORDER BY " +
                Note.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
                note.setUuid(cursor.getString(cursor.getColumnIndex(Note.COLUMN_UUID)));
                note.setItem(cursor.getString(cursor.getColumnIndex(Note.COLUMN_ITEM)));
                note.setPrice(cursor.getString(cursor.getColumnIndex(Note.COLUMN_PRICE)));
                note.setGTIN(cursor.getString(cursor.getColumnIndex(Note.COLUMN_GTIN)));
                note.setWarehouse(cursor.getString(cursor.getColumnIndex(Note.COLUMN_WARE)));
                note.setWid(cursor.getString(cursor.getColumnIndex(Note.COLUMN_WID)));
                note.setManufacturer(cursor.getString(cursor.getColumnIndex(Note.COLUMN_MAN)));
                note.setMid(cursor.getString(cursor.getColumnIndex(Note.COLUMN_MID)));
                note.setGs1(cursor.getString(cursor.getColumnIndex(Note.COLUMN_GS1)));
                note.setRetailer(cursor.getString(cursor.getColumnIndex(Note.COLUMN_RETAILER)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public JSONArray getAllItems() {

        // Select All Query
        String selectQuery = "SELECT " + Note.COLUMN_UUID + "," + Note.COLUMN_PRICE + "," + Note.COLUMN_MAN + "," + Note.COLUMN_MID + "," + Note.COLUMN_WARE + "," + Note.COLUMN_WID + "," + Note.COLUMN_GS1 + "," + Note.COLUMN_RETAILER + "  FROM " + Note.TABLE_NAME + " ORDER BY " +
                Note.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        JSONArray resultSet = new JSONArray();
        JSONObject returnObj = new JSONObject();

        cursor.moveToFirst();
        try {
            while (cursor.isAfterLast() == false) {
                int totalColumn = cursor.getColumnCount();
                JSONObject rowObject = new JSONObject();
                for (int i = 0; i < totalColumn; i++) {
                    if (cursor.getColumnName(i) != null) {

                        if (cursor.getString(i) != null) {
                            Log.d("TAG_NAME ", cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }

                    }
                }
                resultSet.put(rowObject);
                cursor.moveToNext();

            }

        }catch(Exception e){
            Log.d("TAG_NAME ", e.getMessage());
        }
        cursor.close();
        Log.d("TAG_NAME2 ", resultSet.toString());

        return resultSet;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + Note.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public double getTotalPrice() {
        String totalQuery = "SELECT  SUM( " + Note.COLUMN_PRICE + ") as totalp FROM " + Note.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(totalQuery, null);
        cursor.moveToFirst();
        double total = cursor.getDouble(cursor.getColumnIndex("totalp"));
        cursor.close();


        // return count
        return total;
    }

//    public int updateNote(Note note) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(Note.COLUMN_ITEMS, note.getItems());
//
//        // updating row
//        return db.update(Note.TABLE_NAME, values, Note.COLUMN_ID + " = ?",
//                new String[]{String.valueOf(note.getId())});
//    }

    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }

    public void deleteItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(Note.TABLE_NAME, null, null);
        db.close();

        Log.d(TAG, "Deleted all items list from sqlite");
    }
}
