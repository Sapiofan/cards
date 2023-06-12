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

        ImageButton studying = findViewById(R.id.studyId);
        View.OnClickListener handler = new View.OnClickListener() {

            public void onClick(View v) {
                if (v == studying) {
                    Intent intentMain = new Intent(SettingsActivity.this,
                            StudyingActivity.class);
                    SettingsActivity.this.startActivity(intentMain);
                    Log.i("Content ", " Setting layout");
                }
            }
        };

        studying.setOnClickListener(handler);
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
