package com.sapiofan.cards;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sapiofan.cards.adapters.CardAdapter;
import com.sapiofan.cards.adapters.CardItemDecoration;
import com.sapiofan.cards.entities.Card;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudyingActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView recalledCountTextView;
    private TextView totalCountTextView;
    private LinearLayout card;
    private TextView textViewFront;
    private TextView textViewBack;
    private Button forgotButton;
    private Button rememberButton;
    private boolean isFrontVisible = true;
    private int recalledCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studying);

        // Initialize views
        progressBar = findViewById(R.id.progressBar);
        recalledCountTextView = findViewById(R.id.recalledCountTextView);
        totalCountTextView = findViewById(R.id.totalTextView);
        card = findViewById(R.id.card);
        textViewFront = findViewById(R.id.textViewFront);
        textViewBack = findViewById(R.id.textViewBack);
        forgotButton = findViewById(R.id.forgotButton);
        rememberButton = findViewById(R.id.rememberButton);

        List<Card> cards = getCardList();
        int totalCount = cards.size();
        progressBar.setMax(totalCount);
        progressBar.setProgress(recalledCount);
        totalCountTextView.setText(String.valueOf(totalCount));

        forgotButton.setOnClickListener(v -> {
            // Perform action when the "Forgot" button is clicked
        });

        rememberButton.setOnClickListener(v -> {
            recalledCount++;
            int currentProgress = progressBar.getProgress();
            int maxProgress = progressBar.getMax();
            if (currentProgress < maxProgress) {
                progressBar.setProgress(currentProgress + 1);
            }
            recalledCountTextView.setText(String.valueOf(recalledCount));
            textViewFront.setText("New Front");
            textViewBack.setText("New Back");
        });

        // Set card click listener
        card.setOnClickListener(v -> {
            if (isFrontVisible) {
                applyAnimation(textViewFront, textViewBack);
            } else {
                applyAnimation(textViewBack, textViewFront);
            }
            isFrontVisible = !isFrontVisible;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item1:
                // Perform action for Option 1
                return true;
            case R.id.menu_item2:
                // Perform action for Option 2
                return true;
            case R.id.menu_item3:
                // Perform action for Option 3
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void applyAnimation(final View visibleView, final View invisibleView) {
        ObjectAnimator flipOut = ObjectAnimator.ofFloat(visibleView, "rotationY", 0f, 90f);
        flipOut.setDuration(300);
        flipOut.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator flipIn = ObjectAnimator.ofFloat(invisibleView, "rotationY", -90f, 0f);
        flipIn.setDuration(300);
        flipIn.setInterpolator(new AccelerateDecelerateInterpolator());

        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                visibleView.setVisibility(View.GONE);
                flipIn.start();
                invisibleView.setVisibility(View.VISIBLE);
            }
        });

        flipOut.start();
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
