package com.sapiofan.cards.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sapiofan.cards.entities.Card;
import com.sapiofan.cards.entities.Collection;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cards.db";
    private static final String COLLECTIONS = "collections";
    private static final String CARDS = "cards";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + COLLECTIONS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, age INTEGER)";
        db.execSQL(createTableQuery);
        createTableQuery = "CREATE TABLE IF NOT EXISTS " + CARDS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, age INTEGER)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Function to add an object to the table
    public void addCollection(String name, int parent) {
        SQLiteDatabase db = this.getWritableDatabase();
        String insertQuery;
        if (parent == 0) {
            insertQuery = "INSERT INTO " + COLLECTIONS + " (name, parent, in_study) VALUES ('" + name + "', NULL, 1)";
        } else {
            insertQuery = "INSERT INTO " + COLLECTIONS + " (name, parent, in_study) VALUES ('" + name + "', " + parent + ", 1)";
        }
        db.execSQL(insertQuery);
        db.close();
    }

    // Function to read one object
//    public Object readObject(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE id = " + id;
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        // Assuming your object has name and age properties
//        String name = cursor.getString(cursor.getColumnIndex("name"));
//        int age = cursor.getInt(cursor.getColumnIndex("age"));
//
//        cursor.close();
//        db.close();
//
//        // Create and return the object
//        return new Object(name, age);
//    }

    // Function to read all objects
    public List<Object> getObjectsInCollection(int parent_id) {
        List<Object> objects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQueryCollection;
        String selectQueryCards;
        if (parent_id == 0) {
            selectQueryCollection = "SELECT * FROM " + COLLECTIONS + " WHERE parent = NULL";
            selectQueryCards = "SELECT * FROM " + CARDS + " WHERE collection = NULL";
        } else {
            selectQueryCollection = "SELECT * FROM " + COLLECTIONS + " WHERE parent = " + parent_id;
            selectQueryCards = "SELECT * FROM " + CARDS + " WHERE collection = " + parent_id;
        }
        Cursor cursor = db.rawQuery(selectQueryCollection, null);

        if (cursor.moveToFirst()) {
            do {
                // Assuming your object has name and age properties
                String name = cursor.getString(cursor.getColumnIndex("name"));
                boolean inStudy = cursor.getInt(cursor.getColumnIndex("in_study")) > 0;
                int id = cursor.getInt(cursor.getColumnIndex("id"));

                // Create and add the object to the list
                objects.add(new Collection(id, name, inStudy, parent_id));
            } while (cursor.moveToNext());
        }

        cursor.close();

        cursor = db.rawQuery(selectQueryCards, null);
        if (cursor.moveToFirst()) {
            do {
                // Assuming your object has name and age properties
                String text = cursor.getString(cursor.getColumnIndex("text"));
                String translation = cursor.getString(cursor.getColumnIndex("translation"));
                Date repetition = new Date(cursor.getLong(cursor.getColumnIndex("repetition")));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int level = cursor.getInt(cursor.getColumnIndex("level"));

                // Create and add the object to the list
                objects.add(new Card(id, text, translation, repetition, level, parent_id));
            } while (cursor.moveToNext());
        }

        db.close();

        return objects;
    }

    public void removeCollection(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String getQueryCollections = "SELECT * FROM " + COLLECTIONS + " WHERE parent = " + id;
        Cursor cursor = db.rawQuery(getQueryCollections, null);

        if (cursor.moveToFirst()) {
            do {
                int child_collection_id = cursor.getInt(cursor.getColumnIndex("id"));
                removeCollection(child_collection_id);
            } while (cursor.moveToNext());
        }

        cursor.close();
        String deleteQuery = "DELETE FROM " + CARDS + " WHERE collection = " + id;
        db.execSQL(deleteQuery);
        deleteQuery = "DELETE FROM " + COLLECTIONS + " WHERE id = " + id;
        db.execSQL(deleteQuery);
        db.close();
    }

    public void removeObjectsInCollection() {

    }

    public boolean renameCollection(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + COLLECTIONS + " SET name = '" + name + "' WHERE id = " + id;
        db.execSQL(updateQuery);
        db.close();
        return true;
    }

    public void removeCardById(int card_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + CARDS + " WHERE id = " + card_id;
        db.execSQL(deleteQuery);
        db.close();
    }

    private boolean addCard(String text, String translation, int parent) {
        if (parent <= 0) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String insertQuery = "INSERT INTO " + CARDS + " (text, translation, date, level, collection) " +
                "VALUES ('" + text + "', '" + translation + "', '" + dateFormat.format(new Date()) +
                "', " + 1 + "', " + parent + ")";
        db.execSQL(insertQuery);
        db.close();

        return true;
    }

    public boolean addCard(String text, String translation, int parent, boolean reverse) {
        if (reverse) {
            return addCard(text, translation, parent) && addCard(translation, text, parent);
        }
        return addCard(text, translation, parent);
    }

    public void updateCard(int id, String text, String translation, int parent) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + CARDS + " SET text = '" + text + "', translation = '" + translation + "'" +
                ", collection = " + parent + " WHERE id = " + id;
        db.execSQL(updateQuery);
        db.close();
    }

    public Card getCard(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + CARDS + " WHERE id = " + id;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        // Assuming your object has name and age properties
        String text = cursor.getString(cursor.getColumnIndex("text"));
        String translation = cursor.getString(cursor.getColumnIndex("translation"));
        Date repetition = new Date(cursor.getLong(cursor.getColumnIndex("repetition")));
        int level = cursor.getInt(cursor.getColumnIndex("level"));
        int collection = cursor.getInt(cursor.getColumnIndex("collection"));

        cursor.close();
        db.close();

        return new Card(id, text, translation, repetition, level, collection);
    }

    public void moveObjectsToOtherCollection(List<Integer> ids) {

    }
}
