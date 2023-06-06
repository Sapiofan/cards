package com.sapiofan.cards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

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
    }

    private List<Card> getCardList() {
        // Retrieve the list of cards from your data source or API call
        // For demonstration purposes, let's create a dummy list
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(1, "Card 1", "Translation 1", new Date(), 1, 0));
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