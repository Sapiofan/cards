package com.sapiofan.cards.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sapiofan.cards.entities.Collection;

import java.util.ArrayList;
import java.util.List;

public class CollectionTableHandler extends DatabaseHelper {

    public CollectionTableHandler(Context context) {
        super(context);
    }

    public List<Collection> getCollectionsInCollection(int parent_id) {
        List<Collection> collections = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQueryCollection;
        if (parent_id == 0) {
            selectQueryCollection = "SELECT * FROM " + COLLECTIONS + " WHERE parent IS NULL";
        } else {
            selectQueryCollection = "SELECT * FROM " + COLLECTIONS + " WHERE parent = " + parent_id;
        }
        Cursor cursor = db.rawQuery(selectQueryCollection, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                collections.add(getCollection(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();

        return collections;
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
        db.execSQL(insertQuery);
        db.close();
    }

    public Collection getCollectionByName(String name, int parent) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQueryCollection = "SELECT * FROM " + COLLECTIONS + " WHERE parent" + (parent == 0 ? " IS NULL"
                : (" = " + parent)) + " AND name = '" + name + "'";
        Cursor cursor = db.rawQuery(selectQueryCollection, null);

        if (cursor != null && cursor.moveToFirst()) {
            Collection collection = getCollection(cursor);
            cursor.close();
            db.close();
            return collection;
        }

        db.close();

        return null;
    }

    public void removeCollection(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String getQueryCollections = "SELECT * FROM " + COLLECTIONS + " WHERE parent = " + id;
        Cursor cursor = db.rawQuery(getQueryCollections, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int child_collection_id = cursor.getInt(cursor.getColumnIndex("id"));
                removeCollection(child_collection_id);
            } while (cursor.moveToNext());
            cursor.close();
        }

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

    public Collection getParentByChildId(int currentCollection) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + COLLECTIONS + " WHERE id = " + currentCollection;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("parent"));
            selectQuery = "SELECT * FROM " + COLLECTIONS + " where id = " + id;
            cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.moveToFirst()) {
                return getCollection(cursor);
            }
        }

        db.close();

        return null;
    }

    public List<Collection> getCollectionsForCards() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Collection> collections = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + COLLECTIONS + " WHERE for_cards = 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                collections.add(getCollection(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();

        return collections;
    }

    public List<Collection> getAllCollections() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Collection> collections = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + COLLECTIONS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                collections.add(getCollection(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();

        return collections;
    }

    private Collection getCollection(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        int parent_id = cursor.getInt(cursor.getColumnIndex("parent"));
        boolean in_study = cursor.getInt(cursor.getColumnIndex("in_study")) == 1;
        boolean for_cards = cursor.getInt(cursor.getColumnIndex("for_cards")) == 1;

        return new Collection(id, name, in_study, parent_id, for_cards);
    }
}
