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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "cards.db";
    protected static final String COLLECTIONS = "collections";
    protected static final String CARDS = "cards";
    protected static final String WORDS_CHARACTERISTICS = "words";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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

    public CardWord getWordsSize() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + WORDS_CHARACTERISTICS + " WHERE preference_key = 'size'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int size = Integer.parseInt(cursor.getString(cursor.getColumnIndex("preference_value")));
            return new CardWord(size);
        }

        return null;
    }

    public void updateWordsSize(CardWord cardWord) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + WORDS_CHARACTERISTICS + " SET preference_value = '" + cardWord.getSize() +
                "' WHERE preference_key = 'size'";
        db.execSQL(query);
    }
}
