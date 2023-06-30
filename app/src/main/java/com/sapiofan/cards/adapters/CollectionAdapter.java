package com.sapiofan.cards.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sapiofan.cards.R;
import com.sapiofan.cards.entities.Collection;
import com.sapiofan.cards.services.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {
    private List<Collection> collectionList;
    private OnCollectionClickListener onCollectionClickListener;
    private OnSelectionModeChangeListener selectionModeChangeListener;
    private DatabaseHelper databaseHelper;
    private int currentCollection = 0;
    private boolean selectionMode = false;

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
        databaseHelper.addCollection(collectionName, currentCollection, for_cards);
        Collection collection = databaseHelper.getCollectionByName(collectionName, currentCollection);
        collectionList.add(collection);

        notifyDataSetChanged();

        return true;
    }

    public void removeCollections(List<Collection> selectedCollections) {
        for (Collection selectedCollection : selectedCollections) {
            databaseHelper.removeCollection(selectedCollection.getId());
        }

        collectionList.removeAll(selectedCollections);
        notifyDataSetChanged();
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }

    public List<Collection> getSelectedCollections() {
        List<Collection> selectedCollections = new ArrayList<>();
        for (Collection collection : collectionList) {
            if (collection.isSelected()) {
                selectedCollections.add(collection);
            }
        }
        return selectedCollections;
    }

    public int getCurrentCollection() {
        return currentCollection;
    }

    public void setCurrentCollection(int currentCollection) {
        this.currentCollection = currentCollection;
    }

    public void setOnSelectionModeChangeListener(OnSelectionModeChangeListener listener) {
        this.selectionModeChangeListener = listener;
    }

    public class CollectionViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
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
                        if(!selectionMode) {
                            if (onCollectionClickListener != null) {
                                onCollectionClickListener.onCollectionClick(collection);
                            }
                        } else {
                            collection.setSelected(!collection.isSelected());
                            itemView.setSelected(collection.isSelected());
                            if (!collection.isSelected()) {
                                if (selectionModeChangeListener != null) {
                                    selectionModeChangeListener.onSelectionModeChanged(false);
                                }
                            }
                        }
                    }
                }
            });

            buttonHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Collection collection = collectionList.get(position);
                        if(collection.isInStudy()) {
                            buttonHide.setImageResource(R.drawable.sleeping);
                            collection.setInStudy(false);
                            databaseHelper.setCollectionVisibility(collection.getId(), false);
                        } else {
                            buttonHide.setImageResource(R.drawable.nerd);
                            collection.setInStudy(true);
                            databaseHelper.setCollectionVisibility(collection.getId(), true);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(this);
        }

        public void bind(Collection collection) {
            textViewName.setText(collection.getName());
            itemView.setSelected(collection.isSelected());
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Collection collection = collectionList.get(position);
                collection.setSelected(!collection.isSelected());
                itemView.setSelected(collection.isSelected());
                boolean selectionMode = isAnyCollectionSelected();
                setSelectionMode(selectionMode);

                if (selectionModeChangeListener != null) {
                    selectionModeChangeListener.onSelectionModeChanged(selectionMode);
                }
                notifyItemChanged(position);
                return true;
            }
            return false;
        }
    }

    public boolean isAnyCollectionSelected() {
        for (Collection collection : collectionList) {
            if (collection.isSelected()) {
                return true;
            }
        }
        return false;
    }

    public interface OnCollectionClickListener {
        void onCollectionClick(Collection collection);
    }

    public interface OnSelectionModeChangeListener {
        void onSelectionModeChanged(boolean selectionMode);
    }
}
