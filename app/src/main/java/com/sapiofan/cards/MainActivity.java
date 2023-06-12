package com.sapiofan.cards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.sapiofan.cards.adapters.CardAdapter;
import com.sapiofan.cards.adapters.CardItemDecoration;
import com.sapiofan.cards.entities.Card;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assuming you have retrieved the list of cards
        List<Card> cardList = getCardList();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new CardItemDecoration());

        CardAdapter cardAdapter = new CardAdapter(cardList);
        recyclerView.setAdapter(cardAdapter);

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
        View.OnClickListener handler = new View.OnClickListener(){

            public void onClick(View v) {

                if(v==studying){
                    // doStuff
                    Intent intentMain = new Intent(MainActivity.this,
                            StudyingActivity.class);
                    MainActivity.this.startActivity(intentMain);
                    Log.i("Content "," Main layout");
                }
            }
        };

        studying.setOnClickListener(handler);
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
}