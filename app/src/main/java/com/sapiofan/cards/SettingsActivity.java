package com.sapiofan.cards;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sapiofan.cards.adapters.SettingsAdapter;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        List<String> settings = getMenuOptions();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.addItemDecoration(new CardItemDecoration());

        SettingsAdapter settingsAdapter = new SettingsAdapter(settings);
        recyclerView.setAdapter(settingsAdapter);
    }

    public void footerCollectionsButtonClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void footerStudyingButtonClicked(View view) {
        Intent intent = new Intent(this, StudyingActivity.class);
        startActivity(intent);
    }

    public void footerSettingsButtonClicked(View view) {

    }

    private List<String> getMenuOptions() {
        List<String> cards = new ArrayList<>();
        cards.add("Font Size");
        cards.add("Repetitions");
        cards.add("Load Collection");
        cards.add("Instruction");
        cards.add("About");

        return cards;
    }
}
