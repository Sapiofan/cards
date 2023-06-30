package com.sapiofan.cards;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import java.util.List;

public class MainActivity extends AppCompatActivity implements CollectionAdapter.OnCollectionClickListener,
        CollectionAdapter.OnSelectionModeChangeListener {

    private CardAdapter cardAdapter;
    private CollectionAdapter collectionAdapter;
    private List<Card> cardList;

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private Button openModalButton;
    private Dialog modalDialog;

    private DatabaseHelper databaseHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        emptyTextView = findViewById(R.id.emptyTextView);
        openModalButton = findViewById(R.id.addObject);

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

        modalDialog = new Dialog(this);
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
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionAdapter.addNewCollection(collectionName.getText().toString(), forCards.isChecked());
                recyclerView.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
                collectionName.setText("");
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

    private void setCollectionAdapter(Collection collection, RecyclerView recyclerView) {
        List<Object> collectionObjects;
        if (collection == null) {
            collectionObjects = databaseHelper.getObjectsInCollection(0);
        } else {
            collectionObjects = databaseHelper.getObjectsInCollection(collection.getId());
        }
        List<Collection> collectionList = new ArrayList<>();
        for (Object collectionObject : collectionObjects) {
            collectionList.add((Collection) collectionObject);
        }
        collectionAdapter = new CollectionAdapter(collectionList, this, databaseHelper);
        recyclerView.setAdapter(collectionAdapter);
        recyclerView.addItemDecoration(new CollectionDecoration());
        recyclerView.removeItemDecorationAt(0);
        collectionAdapter.setOnSelectionModeChangeListener(this);

        setRecyclerViewVisibility(collectionObjects);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        openModalButton.setText("Add collection");
    }

    private void setCardAdapter(Collection collection) {
        List<Object> collectionObjects = databaseHelper.getObjectsInCollection(collection.getId());
        List<Card> collectionList = new ArrayList<>();
        for (Object collectionObject : collectionObjects) {
            collectionList.add((Card) collectionObject);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addItemDecoration(new CardItemDecoration());

        cardAdapter = new CardAdapter(collectionList, databaseHelper.getWordsSize());
        recyclerView.setAdapter(cardAdapter);
        recyclerView.removeItemDecorationAt(0);

        setRecyclerViewVisibility(collectionObjects);
        openModalButton.setText("Add card");
        manageModalFormForCards();
    }

    @Override
    public void onCollectionClick(Collection collection) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
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
        }
        return true;
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.c_menu_remove:
                // Remove selected collections
                List<Collection> selectedCollections = collectionAdapter.getSelectedCollections();
                collectionAdapter.removeCollections(selectedCollections);
                exitEditMode();
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

    private void setRecyclerViewVisibility(List<Object> collectionObjects) {
        if (collectionObjects.isEmpty()) {
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
    }
}