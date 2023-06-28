package com.sapiofan.cards;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
//        cardList = getCardList();
//        List<Collection> collectionList = getCollectionList();
        List<Object> collectionObjects = databaseHelper.getObjectsInCollection(0);
        List<Collection> collectionList = new ArrayList<>();
        for (Object collectionObject : collectionObjects) {
            collectionList.add((Collection) collectionObject);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        TextView emptyTextView = findViewById(R.id.emptyTextView);


        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        CollectionAdapter collectionAdapter = new CollectionAdapter(collectionList, this, databaseHelper);
        recyclerView.setAdapter(collectionAdapter);
        recyclerView.addItemDecoration(new CollectionDecoration());

        if (collectionList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }

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


        Button openModalButton = findViewById(R.id.addObject);

        Dialog modalDialog = new Dialog(this);
        modalDialog.setContentView(R.layout.modal_add_collection);
        modalDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button cancelButton = modalDialog.findViewById(R.id.cancelButton);
        Button createButton = modalDialog.findViewById(R.id.createButton);
        EditText collectionName = modalDialog.findViewById(R.id.inputEditText);
        CheckBox forCards = modalDialog.findViewById(R.id.forCards);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the modal by dismissing the dialog or hiding the view
                modalDialog.dismiss();
                // Alternatively, if you're using a custom view instead of a dialog, you can use:
                // modalView.setVisibility(View.GONE);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionAdapter.addNewCollection(collectionName.getText().toString(), forCards.isChecked());
                recyclerView.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
                modalDialog.dismiss();
            }
        });

        openModalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modalDialog.show();
            }
        });
    }

    public void footerCollectionsButtonClicked(View view) {

    }

    public void footerStudyingButtonClicked(View view) {
        Intent intent = new Intent(this, StudyingActivity.class);
        startActivity(intent);
    }

    public void footerSettingsButtonClicked(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private List<Collection> getCollectionList() {
        List<Collection> collectionList = new ArrayList<>();
        Collection collection = new Collection(2, "Collection 2", true, 0, true);
        collection.setCards(getCardList());
        collectionList.add(new Collection(1, "Collection 1Collection 1Collection 1", true, 0, true));
        collectionList.add(collection);
        collectionList.add(new Collection(3, "Collection 3", true, 0, true));
        collectionList.add(new Collection(4, "Collection 4", true, 0, true));
        collectionList.add(new Collection(5, "Collection 5", true, 0, true));
        collectionList.add(new Collection(6, "Collection 6", true, 0, true));
        collectionList.add(new Collection(7, "Collection 7", true, 0, true));
        collectionList.add(new Collection(8, "Collection 8", true, 0, true));
        collectionList.add(new Collection(9, "Collection 9", true, 0, true));

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
}