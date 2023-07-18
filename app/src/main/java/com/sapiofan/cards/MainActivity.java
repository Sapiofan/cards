package com.sapiofan.cards;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sapiofan.cards.adapters.CardAdapter;
import com.sapiofan.cards.adapters.CardItemDecoration;
import com.sapiofan.cards.adapters.CollectionAdapter;
import com.sapiofan.cards.adapters.CollectionDecoration;
import com.sapiofan.cards.entities.Card;
import com.sapiofan.cards.entities.Collection;
import com.sapiofan.cards.services.CardTableHandler;
import com.sapiofan.cards.services.CollectionTableHandler;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CollectionAdapter.OnCollectionClickListener,
        CollectionAdapter.OnSelectionModeChangeListener, CardAdapter.OnSelectionModeCardChangeListener {

    private CardAdapter cardAdapter;
    private CollectionAdapter collectionAdapter;

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private Button openModalButton;
    private Dialog modalDialog;

    private final CardTableHandler cardTableHandler = new CardTableHandler(this);
    private final CollectionTableHandler collectionTableHandler = new CollectionTableHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the Toolbar as the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable upArrow = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(
                ((BitmapDrawable) getResources().getDrawable(R.drawable.left_arrow)).getBitmap(),
                24,
                24,
                true
        ));
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        recyclerView = findViewById(R.id.recyclerView);
        emptyTextView = findViewById(R.id.emptyTextView);
        openModalButton = findViewById(R.id.addObject);
        modalDialog = new Dialog(this);

        // default card adapter
        cardAdapter = new CardAdapter(new ArrayList<>(), collectionTableHandler.getWordsSize(), cardTableHandler);
        cardAdapter.setCurrentFolderId(0);
        cardAdapter.setOnSelectionModeChangeListener(this);

        // initial collection adapter
        setCollectionAdapter(null, recyclerView);

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

        openModalButton.setOnClickListener(v -> modalDialog.show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        Collection collection = null;
        if (recyclerView.getAdapter() instanceof CollectionAdapter) {
            CollectionAdapter collectionAdapter = (CollectionAdapter) recyclerView.getAdapter();
            collection = collectionTableHandler.getParentByChildId(collectionAdapter.getCurrentCollection());
            setCollectionAdapter(collection, recyclerView);
            collectionAdapter = (CollectionAdapter) recyclerView.getAdapter();
            if (collection == null) {
                collectionAdapter.setCurrentCollection(0);
            } else {
                collectionAdapter.setCurrentCollection(collection.getId());
            }
        } else if (recyclerView.getAdapter() instanceof CardAdapter) {
            CardAdapter cardAdapter = (CardAdapter) recyclerView.getAdapter();
            collection = collectionTableHandler.getParentByChildId(cardAdapter.getCurrentFolderId());
        }
        setCollectionAdapter(collection, recyclerView);
        if (collection != null) {
            setTitle(collection.getName());
            collectionAdapter.setCurrentCollection(collection.getId());
        } else {
            setTitle("Collections");
            collectionAdapter.setCurrentCollection(0);
        }
        collectionAdapter = (CollectionAdapter) recyclerView.getAdapter();
        return true;
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

    private void setCollectionAdapter(Collection collection, RecyclerView recyclerView) {
        List<Collection> collections;
        if (collection == null) {
            collections = collectionTableHandler.getCollectionsInCollection(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            collections = collectionTableHandler.getCollectionsInCollection(collection.getId());
        }
        collectionAdapter = new CollectionAdapter(collections, this, collectionTableHandler);
        recyclerView.setAdapter(collectionAdapter);
        recyclerView.addItemDecoration(new CollectionDecoration());
        if (recyclerView.getItemDecorationCount() > 1) {
            recyclerView.removeItemDecorationAt(0);
        }
        collectionAdapter.setOnSelectionModeChangeListener(this);
        if (collection != null) {
            collectionAdapter.setCurrentCollection(collection.getId());
        }

        setRecyclerViewVisibility(collections.isEmpty());

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        openModalButton.setText("Add collection");
        manageModalFormForCollections();
    }

    private void setCardAdapter(Collection collection) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        List<Card> cardList = cardTableHandler.getCardsInCollection(collection.getId());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new CardItemDecoration());

        cardAdapter = new CardAdapter(cardList, cardTableHandler.getWordsSize(), cardTableHandler);
        cardAdapter.setCurrentFolderId(collection.getId());
        cardAdapter.setOnSelectionModeChangeListener(this);
        recyclerView.setAdapter(cardAdapter);
        if (recyclerView.getItemDecorationCount() > 1) {
            recyclerView.removeItemDecorationAt(0);
        }

        setRecyclerViewVisibility(cardList.isEmpty());
        openModalButton.setText("Add card");
        manageModalFormForCards();
    }

    @Override
    public void onCollectionClick(Collection collection) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        setTitle(collection.getName());

        if (collection.isForCards()) {
            setCardAdapter(collection);
        } else {
            setCollectionAdapter(collection, recyclerView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (collectionAdapter.isSelectionMode()) {
            getMenuInflater().inflate(R.menu.collection_edit_menu, menu);
            return true;
        }
        if (cardAdapter.isSelectionMode()) {
            getMenuInflater().inflate(R.menu.card_edit_menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public void onSelectionModeChanged(boolean selectionMode) {
        supportInvalidateOptionsMenu();
        if (!selectionMode) {
            exitEditMode();
        } else {
            collectionAdapter.setSelectionMode(true);
        }
    }

    @Override
    public void onSelectionCardModeChanged(boolean selectionMode) {
        supportInvalidateOptionsMenu();
        if (!selectionMode) {
            exitCardEditMode();
        } else {
            cardAdapter.setSelectionMode(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.c_menu_remove:
                // Remove selected collections
                collectionAdapter.removeCollections(collectionAdapter.getSelectedCollections());
                exitEditMode();
                return true;
            case R.id.card_menu_remove:
                // Remove selected cards
                cardAdapter.removeCards(cardAdapter.getSelectedCards());
                exitCardEditMode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void exitEditMode() {
        collectionAdapter.setSelectionMode(false);
        collectionAdapter.notifyDataSetChanged();
        supportInvalidateOptionsMenu();
    }

    private void exitCardEditMode() {
        cardAdapter.setSelectionMode(false);
        cardAdapter.notifyDataSetChanged();
        supportInvalidateOptionsMenu();
    }

    private void setRecyclerViewVisibility(boolean collectionIsEmpty) {
        if (collectionIsEmpty) {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }
    }

    private void manageModalFormForCards() {
        modalDialog.setContentView(R.layout.modal_add_card);
        modalDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        EditText text1 = modalDialog.findViewById(R.id.card_text1);
        EditText text2 = modalDialog.findViewById(R.id.card_text2);
        Button saveButton = modalDialog.findViewById(R.id.saveCollectionButton);
        Switch switchButton = modalDialog.findViewById(R.id.switchButton);

        saveButton.setOnClickListener(v -> {
            cardAdapter.addCard(text1.getText().toString(), text2.getText().toString(), switchButton.isChecked());
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
            text1.setText("");
            text2.setText("");
            switchButton.setChecked(false);
            modalDialog.dismiss();
        });
    }

    private void manageModalFormForCollections() {
        modalDialog.setContentView(R.layout.modal_add_collection);
        modalDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button cancelButton = modalDialog.findViewById(R.id.cancelButton);
        Button createButton = modalDialog.findViewById(R.id.createButton);
        EditText collectionName = modalDialog.findViewById(R.id.inputEditText);
        CheckBox forCards = modalDialog.findViewById(R.id.forCards);

        cancelButton.setOnClickListener(v -> modalDialog.dismiss());

        createButton.setOnClickListener(v -> {
            collectionAdapter.addNewCollection(collectionName.getText().toString(), forCards.isChecked());
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
            collectionName.setText("");
            modalDialog.dismiss();
        });
    }
}