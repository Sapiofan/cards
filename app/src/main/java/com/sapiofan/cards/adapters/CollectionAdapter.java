package com.sapiofan.cards.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sapiofan.cards.R;
import com.sapiofan.cards.entities.Collection;
import com.sapiofan.cards.services.DatabaseHelper;

import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {
    private List<Collection> collectionList;
    private OnCollectionClickListener onCollectionClickListener;
    private DatabaseHelper databaseHelper;
    private int currentCollection = 0;

    public CollectionAdapter(List<Collection> collectionList, OnCollectionClickListener onCollectionClickListener,
                             DatabaseHelper databaseHelper) {
        this.collectionList = collectionList;
        this.onCollectionClickListener = onCollectionClickListener;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collection, parent, false);
        return new CollectionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        Collection collection = collectionList.get(position);
        if (collection != null) {
            holder.bind(collection);
        }
    }

    @Override
    public int getItemCount() {
        return collectionList.size();
    }

    public boolean addNewCollection(String collectionName, boolean for_cards) {
        System.out.println(collectionName);
        System.out.println(for_cards);
        databaseHelper.addCollection(collectionName, currentCollection, for_cards);
        Collection collection = databaseHelper.getCollectionByName(collectionName, currentCollection);
        System.out.println(collection);
        collectionList.add(collection);

        notifyDataSetChanged();

        return true;
    }

    public int getCurrentCollection() {
        return currentCollection;
    }

    public void setCurrentCollection(int currentCollection) {
        this.currentCollection = currentCollection;
    }

    public class CollectionViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private ImageButton buttonHide;

        public CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewFront);
            buttonHide = itemView.findViewById(R.id.buttonHide);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Collection collection = collectionList.get(position);
                        if (onCollectionClickListener != null) {
                            onCollectionClickListener.onCollectionClick(collection);
                        }
                    }
                }
            });

            buttonHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        buttonHide.setImageResource(R.drawable.sleeping);
                    }
                }
            });
        }

        public void bind(Collection collection) {
            textViewName.setText(collection.getName());
        }
    }

    public interface OnCollectionClickListener {
        void onCollectionClick(Collection collection);
    }

    public interface OnButtonClickListener {
        void onButtonClicked(Collection collection);
    }
}
