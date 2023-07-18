package com.sapiofan.cards.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sapiofan.cards.entities.Card;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CardTableHandler extends DatabaseHelper {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public CardTableHandler(Context context) {
        super(context);
    }

    public List<Card> getCardsInCollection(int parent_id) {
        List<Card> cards = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQueryCards;
        if (parent_id == 0) {
            selectQueryCards = "SELECT * FROM " + CARDS + " WHERE collection IS NULL";
        } else {
            selectQueryCards = "SELECT * FROM " + CARDS + " WHERE collection = " + parent_id;
        }

        Cursor cursor = db.rawQuery(selectQueryCards, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                cards.add(getCard(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();

        return cards;
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
        String insertQuery = "INSERT INTO " + CARDS + " (text, translation, date, level, collection) " +
                "VALUES ('" + text + "', '" + translation + "', '" + dateFormat.format(Date.from(Instant.now())) +
                "', " + 0 + ", " + parent + ")";
        db.execSQL(insertQuery);
        db.close();

        return true;
    }

    public boolean addCard(String text, String translation, int parent, boolean reverse) {
        return reverse ? addCard(text, translation, parent) && addCard(translation, text, parent) :
                addCard(text, translation, parent);
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

        Card card = null;
        if (cursor != null && cursor.moveToFirst()) {
            card = getCard(cursor);
            cursor.close();
        }

        db.close();

        return card;
    }

    public List<Card> findCards(String text1, String text2, int currentFolderId) {
        List<Card> cards = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + CARDS + " WHERE collection = " + currentFolderId + " and " +
                "((text = '" + text1 + "' and translation = '" + text2 + "')" +
                " or (text = '" + text2 + "' and translation = '" + text1 + "'))";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                cards.add(getCard(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();

        return cards;
    }

    public List<Card> getAllVisibleCards() {
        List<Card> cards = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String currentDate = dateFormat.format(new Date());
        String selectQuery = "SELECT cards.*, collections.id AS collection_id FROM " + CARDS + " INNER JOIN collections " +
                "ON collections.id = cards.collection " +
                "WHERE collections.in_study = 1 AND datetime(cards.date) <= datetime('" + currentDate + "')";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                cards.add(getCard(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();

        return cards;
    }

    public void updateCardsLevel(List<Card> higherLevel, List<Card> lowerLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (higherLevel.size() != 0) {
            try {
                db.beginTransaction();

                for (Card card : higherLevel) {
                    int lastPeriod = card.getLastPeriod() + 1 >= Period.values().length ? Period.values().length - 1
                            : card.getLastPeriod() + 1;
                    long date = Date.from(Instant.now()).getTime() + Period.values()[lastPeriod].getSeconds() * 1000L;
                    String query = "UPDATE " + CARDS +
                            " SET level = '" + lastPeriod +
                            "', date = " + date +
                            " WHERE id = " + card.getId();
                    db.execSQL(query);
                    card.setLastPeriod(lastPeriod);
                    card.setNextRepetition(new Date(date));
                }

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        if (lowerLevel.size() != 0) {
            try {
                db.beginTransaction();
                for (Card card : lowerLevel) {
                    int lastPeriod = Math.max(card.getLastPeriod() - 1, 0);
                    long date = new Date().getTime() + Period.values()[lastPeriod].getSeconds() * 1000L;
                    String query = "UPDATE " + CARDS + " SET level = '" + (lastPeriod) + "', date = "
                            + dateFormat.format(date) +
                            " WHERE id = " + card.getId();
                    db.execSQL(query);
                    card.setLastPeriod(lastPeriod);
                    card.setNextRepetition(new Date(date));
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        db.close();
    }

    @SuppressLint("Range")
    private Card getCard(Cursor cursor) {
        String text = cursor.getString(cursor.getColumnIndex("text"));
        String translation = cursor.getString(cursor.getColumnIndex("translation"));
        Date repetition = new Date(cursor.getLong(cursor.getColumnIndex("date")));
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        int parent_id = cursor.getInt(cursor.getColumnIndex("collection"));
        int level = cursor.getInt(cursor.getColumnIndex("level"));

        return new Card(id, text, translation, repetition, level, parent_id);
    }
}
