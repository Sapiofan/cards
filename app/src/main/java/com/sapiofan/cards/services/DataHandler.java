package com.sapiofan.cards.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapiofan.cards.entities.Card;
import com.sapiofan.cards.entities.Collection;
import com.sapiofan.cards.entities.CollectionObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataHandler {
    public boolean addCollection(Context context, Collection collection) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        ObjectMapper mapper = new ObjectMapper();
        String jsonCollection;
        try {
            jsonCollection = mapper.writeValueAsString(collection);
        } catch (IOException e) {
            Log.e("Cards", "Impossible convert collection to json", e);
            return false;
        }

        editor.putString(collection.getName(), jsonCollection);
        editor.apply();



        // Retrieving the object
        String serializedObject = sharedPreferences.getString(collection.getName(), null);
        try {
            Collection collection1 = mapper.readValue(serializedObject, Collection.class);
        } catch (IOException e) {
            Log.e("Cards", "Impossible convert collection to json", e);
            return false;
        }

        return true;
    }

    public List<CollectionObject> getObjectsInCollection(Context context, String name) {
        List<CollectionObject> objects = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if(name == null) {
            Map<String, ?> serializedObject = sharedPreferences.getAll();

            return null;
        }

        String serializedObject = sharedPreferences.getString(name, null);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Collection collection = mapper.readValue(serializedObject, Collection.class);

        } catch (IOException e) {
            Log.e("Cards", "Impossible convert collection to json", e);
            return null;
        }

        return null;
    }

    public boolean removeCollection(Context context, String name) {
        if(name == null) {
            return false;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String serializedObject = sharedPreferences.getString(name, null);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Collection collection = mapper.readValue(serializedObject, Collection.class);
        } catch (IOException e) {
            Log.e("Cards", "Impossible convert collection to json", e);
            return false;
        }

        return true;
    }

    public boolean removeObjectsInCollection(Context context, String name, List<CollectionObject> objects) {
        return true;
    }

    public boolean renameCollection(Context context, String name) {
        return true;
    }

    public boolean addCard(Context context, String collection) {
        return true;
    }

    public boolean addCard(Context context, String collection, boolean reverseTranslation) {
        return true;
    }

    public boolean editCard(Context context, String collection, Card init, Card changed) {
        return true;
    }

    public boolean moveObjectsToOtherCollection(Context context, String initCollection, String destCollection,
                                                List<CollectionObject> objects) {
        return true;
    }
}
