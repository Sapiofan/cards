package com.sapiofan.cards;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sapiofan.cards.entities.CardWord;
import com.sapiofan.cards.services.DatabaseHelper;

public class SizeActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView textViewFront;
    private Button defaultSizeButton;
    private final int MIN_TEXT_SIZE = 28;
    private final float TEXT_SIZE_STEP = (float) 0.7;
    private int LAST_TEXT_SIZE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_size);

        // Initialize views
        textViewFront = findViewById(R.id.textViewFront);
        seekBar = findViewById(R.id.seekBar);
        defaultSizeButton = findViewById(R.id.button2);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        CardWord word = databaseHelper.getWordsSize();

        // Get the current text size of the TextView
        textViewFront.setTextSize(TypedValue.COMPLEX_UNIT_PX, word.getSize());
        LAST_TEXT_SIZE = word.getSize();

        // Convert the text size to a suitable range for the SeekBar
        int progress = convertTextSizeToProgress(word.getSize());

        // Set the SeekBar progress to the calculated value
        seekBar.setProgress(progress);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Calculate the text size based on the SeekBar progress
                float textSize = convertProgressToTextSize(progress);
                LAST_TEXT_SIZE = (int) textSize;

                // Set the new text size for the TextView
                textViewFront.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed for this implementation
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed for this implementation
            }

            private float convertProgressToTextSize(int progress) {
                // Convert progress to the corresponding text size
                // Adjust the calculation based on your desired range and scaling
                return MIN_TEXT_SIZE + (progress * TEXT_SIZE_STEP);
            }
        });

        defaultSizeButton.setOnClickListener(v -> {
            databaseHelper.updateWordsSize(new CardWord(38));
            LAST_TEXT_SIZE = 38;
            seekBar.setProgress(38);
        });

        ImageButton studying = findViewById(R.id.studyId);
        ImageButton collections = findViewById(R.id.collectionId);
        View.OnClickListener handler = new View.OnClickListener() {

            public void onClick(View v) {
                if (v == studying) {
                    databaseHelper.updateWordsSize(new CardWord(LAST_TEXT_SIZE));
                    Intent intentMain = new Intent(SizeActivity.this,
                            StudyingActivity.class);
                    SizeActivity.this.startActivity(intentMain);
                    Log.i("Content ", " Setting layout");
                }
            }
        };

        studying.setOnClickListener(handler);

        collections.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (v == collections) {
                    databaseHelper.updateWordsSize(new CardWord(LAST_TEXT_SIZE));
                    Intent intentMain = new Intent(SizeActivity.this,
                            MainActivity.class);
                    SizeActivity.this.startActivity(intentMain);
                    Log.i("Content ", " Setting layout");
                }
            }
        });
    }

    private int convertTextSizeToProgress(float textSize) {
        // Convert textSize to a suitable progress value
        // Adjust the calculation based on your desired range and scaling
        return (int) ((textSize - MIN_TEXT_SIZE) / TEXT_SIZE_STEP);
    }
}
