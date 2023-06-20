package com.sapiofan.cards;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sapiofan.cards.adapters.CardAdapter;
import com.sapiofan.cards.adapters.CardItemDecoration;
import com.sapiofan.cards.adapters.CollectionAdapter;
import com.sapiofan.cards.adapters.CollectionDecoration;
import com.sapiofan.cards.entities.Card;
import com.sapiofan.cards.entities.Collection;
import com.sapiofan.cards.services.DatabaseHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CollectionAdapter.OnCollectionClickListener {

    private CardAdapter cardAdapter;
    private List<Card> cardList;

    private DatabaseHelper databaseHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assuming you have retrieved the list of cards
        cardList = getCardList();
        List<Collection> collectionList = getCollectionList();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);


        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        CollectionAdapter collectionAdapter = new CollectionAdapter(collectionList, this);
        recyclerView.setAdapter(collectionAdapter);
        recyclerView.addItemDecoration(new CollectionDecoration());

//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        recyclerView.addItemDecoration(new CardItemDecoration());
//
//        CardAdapter cardAdapter = new CardAdapter(cardList, databaseHelper.getWordsSize());
//        recyclerView.setAdapter(cardAdapter);

        EditText searchEditText = findViewById(R.id.searchEditText);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter cards as the user types
                String query = charSequence.toString();
                cardAdapter.filter(query);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        });

        ImageButton studying = findViewById(R.id.studyId);
        View.OnClickListener handler = v -> {
            if (v == studying) {
                Intent intentMain = new Intent(MainActivity.this,
                        StudyingActivity.class);
                MainActivity.this.startActivity(intentMain);
                Log.i("Content ", " Main layout");
            }
        };

        studying.setOnClickListener(handler);

        ImageButton settings = findViewById(R.id.settingsId);

        View.OnClickListener settingsHandler = v -> {
            if (v == settings) {
                Intent intentMain = new Intent(MainActivity.this,
                        SettingsActivity.class);
                MainActivity.this.startActivity(intentMain);
                Log.i("Content ", " Main layout");
            }
        };

        settings.setOnClickListener(settingsHandler);
    }

    private List<Collection> getCollectionList() {
        List<Collection> collectionList = new ArrayList<>();
        Collection collection = new Collection(2, "Collection 2", true, 0);
        collection.setCards(getCardList());
        collectionList.add(new Collection(1, "Collection 1Collection 1Collection 1", true, 0));
        collectionList.add(collection);
        collectionList.add(new Collection(3, "Collection 3", true, 0));
        collectionList.add(new Collection(4, "Collection 4", true, 0));
        collectionList.add(new Collection(5, "Collection 5", true, 0));
        collectionList.add(new Collection(6, "Collection 6", true, 0));
        collectionList.add(new Collection(7, "Collection 7", true, 0));
        collectionList.add(new Collection(8, "Collection 8", true, 0));
        collectionList.add(new Collection(9, "Collection 9", true, 0));

        return collectionList;
    }

    private List<Card> getCardList() {
        // Retrieve the list of cards from your data source or API call
        // For demonstration purposes, let's create a dummy list
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(1, "Card 1", "Translation 1 Translation 1Translation 1Translation 1Translation 1Translation 1Translation 1Translation 1Translation 1", new Date(), 1, 0));
        cards.add(new Card(2, "Card 2", "Translation 2", new Date(), 1, 0));
        cards.add(new Card(3, "Card 3", "Translation 3", new Date(), 1, 0));
        cards.add(new Card(3, "Card 3", "Translation 3", new Date(), 1, 0));
        cards.add(new Card(3, "Card 3", "Translation 3", new Date(), 1, 0));
        cards.add(new Card(3, "Card 3", "Translation 3", new Date(), 1, 0));
        cards.add(new Card(3, "Card 3", "Translation 3", new Date(), 1, 0));
        cards.add(new Card(3, "Card 3", "Translation 3", new Date(), 1, 0));
        cards.add(new Card(3, "Card 3", "Translation 3", new Date(), 1, 0));
        cards.add(new Card(3, "Card 3", "Translation 3", new Date(), 1, 0));
        cards.add(new Card(3, "Card 3", "Translation 3", new Date(), 1, 0));
        cards.add(new Card(3, "Card 3", "Translation 3", new Date(), 1, 0));

        return cards;
    }

    @Override
    public void onCollectionClick(Collection collection) {
        // Handle collection click event
        // Retrieve new cards from the clicked collection
        List<Card> newCards = collection.getCards();

        // Update the RecyclerView with the new card list
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cardAdapter = new CardAdapter(newCards, databaseHelper.getWordsSize());
        recyclerView.setAdapter(cardAdapter);
        recyclerView.removeItemDecorationAt(0);
        recyclerView.addItemDecoration(new CardItemDecoration());
    }

//    @Override
//    public void onButtonClicked(Collection collection) {
//        // Handle button click event for the clicked collection
//        Toast.makeText(this, "Button clicked for collection: " + collection.getName(), Toast.LENGTH_SHORT).show();
//    }
}