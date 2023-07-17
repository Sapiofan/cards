package com.sapiofan.cards;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sapiofan.cards.adapters.CardAdapter;
import com.sapiofan.cards.adapters.CardItemDecoration;
import com.sapiofan.cards.entities.Card;
import com.sapiofan.cards.entities.CardWord;
import com.sapiofan.cards.entities.Collection;
import com.sapiofan.cards.services.DatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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

    private final Map<Card, Boolean> rememberedWords = new HashMap<>();
    private final Map<Card, Integer> forgotWords = new HashMap<>();
    private List<Card> cards;
    private List<Collection> collectionsForCards;
    private Map<Collection, String> paths;
    private Card[] currentCard = new Card[1];

    private final DatabaseHelper databaseHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studying);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Studying");

        // Initialize views
        CardWord cardWord = databaseHelper.getWordsSize();

        progressBar = findViewById(R.id.progressBar);
        recalledCountTextView = findViewById(R.id.recalledCountTextView);
        totalCountTextView = findViewById(R.id.totalTextView);
        card = findViewById(R.id.card);
        textViewFront = findViewById(R.id.textViewFront);
        textViewBack = findViewById(R.id.textViewBack);
        forgotButton = findViewById(R.id.forgotButton);
        rememberButton = findViewById(R.id.rememberButton);

        textViewFront.setTextSize(TypedValue.COMPLEX_UNIT_PX, cardWord.getSize());
        textViewBack.setTextSize(TypedValue.COMPLEX_UNIT_PX, cardWord.getSize());

        Random random = new Random();
        cards = databaseHelper.getAllVisibleCards();
        for (Card cardFromDB : cards) {
            rememberedWords.put(cardFromDB, false);
            forgotWords.put(cardFromDB, 0);
        }

        if (cards.size() > 0) {
            currentCard[0] = cards.get(Math.abs(random.nextInt()) % cards.size());
            textViewFront.setText(currentCard[0].getText());
            textViewBack.setText(currentCard[0].getTranslation());
        } else {
            hideProgressElements();
            TextView desc = findViewById(R.id.description);
            desc.setText("For now all cards are repeated. Add new words or wait for the next repetition");
            desc.setVisibility(View.VISIBLE);
        }

        int totalCount = cards.size();
        progressBar.setMax(totalCount);
        progressBar.setProgress(recalledCount);
        totalCountTextView.setText(String.valueOf(totalCount));

        forgotButton.setOnClickListener(v -> {
            while (true) {
                Card recallCard = cards.get(Math.abs(random.nextInt()) % cards.size());
                if (!rememberedWords.get(recallCard)) {
                    forgotWords.put(currentCard[0], forgotWords.get(currentCard[0]) + 1);
                    currentCard[0] = recallCard;
                    textViewFront.setText(recallCard.getText());
                    textViewBack.setText(recallCard.getTranslation());
                    break;
                }
            }
        });

        rememberButton.setOnClickListener(v -> {
            rememberedWords.put(currentCard[0], true);

            recalledCount++;
            int currentProgress = progressBar.getProgress();
            int maxProgress = progressBar.getMax();
            if (recalledCount < maxProgress) {
                progressBar.setProgress(currentProgress + 1);
            } else {
                hideProgressElements();
                handleStudyingResults();
                return;
            }
            recalledCountTextView.setText(String.valueOf(recalledCount));
            while (true) {
                Card recallCard = cards.get(Math.abs(random.nextInt()) % cards.size());
                if (!rememberedWords.get(recallCard)) {
                    currentCard[0] = recallCard;
                    textViewFront.setText(recallCard.getText());
                    textViewBack.setText(recallCard.getTranslation());
                    break;
                }
            }
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

    private void handleStudyingResults() {
        TextView desc = findViewById(R.id.description);
        desc.setText("Repeated words: " + rememberedWords.size());
        supportInvalidateOptionsMenu();
        desc.setVisibility(View.VISIBLE);
        List<Card> higherLevel = new ArrayList<>();
        List<Card> lowerLevel = new ArrayList<>();
        for (Map.Entry<Card, Integer> cardEntry : forgotWords.entrySet()) {
            if (cardEntry.getValue() < 2) {
                higherLevel.add(cardEntry.getKey());
            } else if (cardEntry.getValue() >= 4) {
                lowerLevel.add(cardEntry.getKey());
            }
        }

        databaseHelper.updateCardsLevel(higherLevel, lowerLevel);

    }

    private void hideProgressElements() {
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout2);
        LinearLayout linearLayout = findViewById(R.id.buttonsContainer);
        constraintLayout.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        card.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (rememberedWords.size() != 0) {
            getMenuInflater().inflate(R.menu.menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_card:
                rememberedWords.remove(currentCard[0]);
                forgotWords.remove(currentCard[0]);
                if (rememberedWords.size() == 0) {
                    hideProgressElements();
                    TextView desc = findViewById(R.id.description);
                    desc.setText("For now all cards are repeated. Add new words or wait for the next repetition");
                    desc.setVisibility(View.VISIBLE);
                } else if (rememberedWords.values().stream().filter(value -> value).count() >= progressBar.getMax()) {
                    hideProgressElements();
                    handleStudyingResults();
                }
                databaseHelper.removeCardById(currentCard[0].getId());
                progressBar.setMax(rememberedWords.size());
                totalCountTextView.setText(String.valueOf(rememberedWords.size()));
                cards.remove(currentCard[0]);
                setRandomCard();
                return true;
            case R.id.edit_card:
                Dialog modalDialog = new Dialog(this);
                modalDialog.show();
                modalDialog.setContentView(R.layout.modal_edit_card);
                modalDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                EditText text1 = modalDialog.findViewById(R.id.edit_card_text1);
                EditText text2 = modalDialog.findViewById(R.id.edit_card_text2);
                Button saveButton = modalDialog.findViewById(R.id.saveCardButton);

                text1.setText(currentCard[0].getText());
                text2.setText(currentCard[0].getTranslation());

                collectionsForCards = databaseHelper.getAllCollections();
                List<String> collectionNames = convertCollectionsToString(collectionsForCards);
                Spinner spinner = modalDialog.findViewById(R.id.collectionsSpinner);
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                        R.layout.spinner_item, collectionNames);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);
                final String[] selected = {buildPath(collectionsForCards,
                        collectionsForCards.stream()
                                .filter(collection -> collection.getId() == currentCard[0].getCollection_id())
                                .collect(Collectors.toList()).get(0), new StringBuilder())};
                spinner.setSelection(collectionNames.indexOf(selected[0]), true);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selected[0] = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int parent_id = getParentIdByPath(selected[0]);
                        databaseHelper.updateCard(currentCard[0].getId(), text1.getText().toString(),
                                text2.getText().toString(), parent_id);
                        int progress = forgotWords.get(currentCard[0]);
                        rememberedWords.remove(currentCard[0]);
                        forgotWords.remove(currentCard[0]);
                        cards.remove(currentCard[0]);
                        Card card = new Card(currentCard[0].getId(), text1.getText().toString(),
                                text2.getText().toString(), currentCard[0].getNextRepetition(),
                                currentCard[0].getLastPeriod(), parent_id);
                        rememberedWords.put(card, false);
                        forgotWords.put(card, progress);
                        cards.add(card);
                        textViewFront.setText(text1.getText());
                        textViewBack.setText(text2.getText());
                        modalDialog.dismiss();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int getParentIdByPath(String path) {
        return paths.entrySet().stream()
                .filter(collectionStringEntry -> collectionStringEntry.getValue().equals(path))
                .findFirst().map(collectionStringEntry -> collectionStringEntry.getKey().getId())
                .orElse(0);
    }

    private void setRandomCard() {
        Random random = new Random();
        boolean notAllWordsRecalled = rememberedWords.values().stream().anyMatch(value -> !value);
        while (notAllWordsRecalled) {
            Card recallCard = cards.get(Math.abs(random.nextInt()) % cards.size());
            if (!rememberedWords.get(recallCard)) {
                currentCard[0] = recallCard;
                textViewFront.setText(recallCard.getText());
                textViewBack.setText(recallCard.getTranslation());
                return;
            }
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

    public void footerCollectionsButtonClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void footerStudyingButtonClicked(View view) {

    }

    public void footerSettingsButtonClicked(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private List<String> convertCollectionsToString(List<Collection> collections) {
        List<String> list = new ArrayList<>();
        paths = new HashMap<>();
        for (Collection collection : collections) {
            if (collection.isForCards()) {
                String s = buildPath(collections, collection, new StringBuilder());
                paths.put(collection, s);
                list.add(s);
            }
        }
        return list;
    }

    private String buildPath(List<Collection> collections, Collection collection, StringBuilder stringBuilder) {
        if(stringBuilder.length() == 0) {
            return buildPath(collections, collection, stringBuilder.append(collection.getName()));
        }

        if(collection.getParent() != 0) {
            Collection parent = collections.stream()
                    .filter(collection1 -> collection1.getId() == collection.getParent())
                    .collect(Collectors.toList()).get(0);
            return buildPath(collections, parent, stringBuilder.append(".").append(parent.getName()));
        }

        stringBuilder.append(".").append("root");

        String[] dividedPath = stringBuilder.toString().split("\\.");

        Collections.reverse(Arrays.asList(dividedPath));

        return String.join(".", dividedPath);
    }
}
