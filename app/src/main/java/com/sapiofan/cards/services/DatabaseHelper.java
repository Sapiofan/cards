package com.sapiofan.cards.services;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sapiofan.cards.entities.Card;
import com.sapiofan.cards.entities.CardWord;
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
    private static final String WORDS_CHARACTERISTICS = "words";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + COLLECTIONS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, parent INTEGER, in_study INTEGER, for_cards INTEGER)";
        db.execSQL(createTableQuery);
        createTableQuery = "CREATE TABLE IF NOT EXISTS " + CARDS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, text TEXT, translation TEXT, " +
                "date LONG, level INTEGER, collection INTEGER)";
        db.execSQL(createTableQuery);

        createTableQuery = "CREATE TABLE IF NOT EXISTS " + WORDS_CHARACTERISTICS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, preference_key TEXT, preference_value TEXT)";
        db.execSQL(createTableQuery);
        addDefaultWordSize(db);
    }

    private void addDefaultWordSize(SQLiteDatabase db) {
        if (DatabaseUtils.queryNumEntries(db, WORDS_CHARACTERISTICS) < 1) {
            String query = "INSERT INTO " + WORDS_CHARACTERISTICS + " (preference_key, preference_value) " +
                    "VALUES ('size', 18)";
            db.execSQL(query);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addCollection(String name, int parent, boolean forCards) {
        SQLiteDatabase db = this.getWritableDatabase();
        String insertQuery;
        if (parent == 0) {
            insertQuery = "INSERT INTO " + COLLECTIONS + " (name, parent, in_study, for_cards) " +
                    "VALUES ('" + name + "', NULL, 1, " + (forCards ? 1 : 0) + ")";
        } else {
            insertQuery = "INSERT INTO " + COLLECTIONS + " (name, parent, in_study, for_cards) " +
                    "VALUES ('" + name + "', " + parent + ", 1, " + (forCards ? 1 : 0) + ")";
        }
        System.out.println(insertQuery);
        db.execSQL(insertQuery);
        db.close();
    }

    public Collection getCollectionByName(String name, int parent) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQueryCollection = "SELECT * FROM " + COLLECTIONS + " WHERE parent" + (parent == 0 ? " IS NULL"
                : (" = " + parent)) + " and name = '" + name + "'";
        System.out.println(selectQueryCollection);
        Cursor cursor = db.rawQuery(selectQueryCollection, null);

        if (cursor.moveToFirst()) {
            System.out.println("In cursor");

            boolean inStudy = cursor.getInt(cursor.getColumnIndex("in_study")) > 0;
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            boolean isForCards = cursor.getInt(cursor.getColumnIndex("for_cards")) > 0;

            cursor.close();
            db.close();
            return new Collection(id, name, inStudy, parent, isForCards);
        }
        cursor.close();
        db.close();

        return null;
    }

    public List<Object> getObjectsInCollection(int parent_id) {
        List<Object> objects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQueryCollection;
        String selectQueryCards;
        if (parent_id == 0) {
            selectQueryCollection = "SELECT * FROM " + COLLECTIONS + " WHERE parent IS NULL";
            selectQueryCards = "SELECT * FROM " + CARDS + " WHERE collection IS NULL";
        } else {
            selectQueryCollection = "SELECT * FROM " + COLLECTIONS + " WHERE parent = " + parent_id;
            selectQueryCards = "SELECT * FROM " + CARDS + " WHERE collection = " + parent_id;
        }
        Cursor cursor = db.rawQuery(selectQueryCollection, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                boolean inStudy = cursor.getInt(cursor.getColumnIndex("in_study")) > 0;
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                boolean isForCards = cursor.getInt(cursor.getColumnIndex("for_cards")) > 0;

                objects.add(new Collection(id, name, inStudy, parent_id, isForCards));
            } while (cursor.moveToNext());
        }

        cursor.close();

        cursor = db.rawQuery(selectQueryCards, null);
        if (cursor.moveToFirst()) {
            do {
                String text = cursor.getString(cursor.getColumnIndex("text"));
                String translation = cursor.getString(cursor.getColumnIndex("translation"));
                Date repetition = new Date(cursor.getLong(cursor.getColumnIndex("date")));
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

    public void renameCollection(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + COLLECTIONS + " SET name = '" + name + "' WHERE id = " + id;
        db.execSQL(updateQuery);
        db.close();
    }

    public void setCollectionVisibility(int id, boolean visibility) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + COLLECTIONS + " SET in_study = '" + (visibility ? 1 : 0) + "' WHERE id = " + id;
        db.execSQL(updateQuery);
        db.close();
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
                "', " + 1 + ", " + parent + ")";
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

        String text = cursor.getString(cursor.getColumnIndex("text"));
        String translation = cursor.getString(cursor.getColumnIndex("translation"));
        Date repetition = new Date(cursor.getLong(cursor.getColumnIndex("date")));
        int level = cursor.getInt(cursor.getColumnIndex("level"));
        int collection = cursor.getInt(cursor.getColumnIndex("collection"));

        cursor.close();
        db.close();

        return new Card(id, text, translation, repetition, level, collection);
    }

    public List<Card> findCards(String text1, String text2, int currentFolderId) {
        List<Card> cards = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + CARDS + " WHERE collection = " + currentFolderId + " and " +
                "((text = '" + text1 + "' and translation = '" + text2 + "')" +
                " or (text = '"+ text2 + "' and translation = '" + text1 + "'))";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String text = cursor.getString(cursor.getColumnIndex("text"));
                String translation = cursor.getString(cursor.getColumnIndex("translation"));
                Date repetition = new Date(cursor.getLong(cursor.getColumnIndex("date")));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int level = cursor.getInt(cursor.getColumnIndex("level"));

                // Create and add the object to the list
                cards.add(new Card(id, text, translation, repetition, level, currentFolderId));
            } while (cursor.moveToNext());
        }

        return cards;
    }

    public void moveObjectsToOtherCollection(List<Integer> ids) {

    }

    public CardWord getWordsSize() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + WORDS_CHARACTERISTICS + " WHERE preference_key = 'size'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        int size = Integer.parseInt(cursor.getString(cursor.getColumnIndex("preference_value")));

        return new CardWord(size);
    }

    public void updateWordsSize(CardWord cardWord) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + WORDS_CHARACTERISTICS + " SET preference_value = '" + cardWord.getSize() +
                "' WHERE preference_key = 'size'";
        db.execSQL(query);
    }
}
