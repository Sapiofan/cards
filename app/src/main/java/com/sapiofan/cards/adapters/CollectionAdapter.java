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

import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {
    private List<Collection> collectionList;
    private OnCollectionClickListener onCollectionClickListener;

    public CollectionAdapter(List<Collection> collectionList, OnCollectionClickListener onCollectionClickListener) {
        this.collectionList = collectionList;
        this.onCollectionClickListener = onCollectionClickListener;
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
        holder.bind(collection);
    }

    @Override
    public int getItemCount() {
        return collectionList.size();
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
                        // Handle buttonHide click event
                        // You can perform actions specific to the button click here
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
