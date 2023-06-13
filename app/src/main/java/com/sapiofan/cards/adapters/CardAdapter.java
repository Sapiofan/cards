package com.sapiofan.cards.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sapiofan.cards.R;
import com.sapiofan.cards.entities.Card;
import com.sapiofan.cards.entities.CardWord;
import com.sapiofan.cards.services.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private List<Card> cardList;
    private List<Card> filteredCardList;
    private CardWord cardWord;

    public CardAdapter(List<Card> cardList, CardWord cardWord) {
        this.cardList = cardList;
        this.filteredCardList = new ArrayList<>(cardList);
        this.cardWord = cardWord;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Card card = filteredCardList.get(position);
        holder.textViewFront.setText(card.getText());
        holder.textViewFront.setTextSize(TypedValue.COMPLEX_UNIT_PX, cardWord.getSize());
        holder.textViewBack.setText(card.getTranslation());
        holder.textViewBack.setTextSize(TypedValue.COMPLEX_UNIT_PX, cardWord.getSize());

        if (card.isFlipped()) {
            holder.textViewFront.setVisibility(View.GONE);
            holder.textViewBack.setVisibility(View.VISIBLE);
        } else {
            holder.textViewFront.setVisibility(View.VISIBLE);
            holder.textViewBack.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return filteredCardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFront;
        TextView textViewBack;
        boolean isFrontVisible = true;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFront = itemView.findViewById(R.id.textViewFront);
            textViewBack = itemView.findViewById(R.id.textViewBack);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Card card = cardList.get(position);
                    if (isFrontVisible) {
                        applyAnimation(textViewFront, textViewBack);
                    } else {
                        applyAnimation(textViewBack, textViewFront);
                    }
                    isFrontVisible = !isFrontVisible;
                }
            });
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
    }

    public void filter(String query) {
        query = query.toLowerCase().trim(); // Convert query to lowercase and remove leading/trailing spaces

        filteredCardList.clear(); // Clear previous filtered cards

        if (query.isEmpty()) {
            filteredCardList.addAll(cardList); // If query is empty, show all cards
        } else {
            for (Card card : cardList) {
                if (card.getText().toLowerCase().contains(query)) {
                    filteredCardList.add(card); // Add card to filteredCardList if its text contains the query
                }
            }
        }

        notifyDataSetChanged(); // Notify adapter about the data change
    }
}