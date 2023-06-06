package com.sapiofan.cards.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sapiofan.cards.R;
import com.sapiofan.cards.entities.Card;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private List<Card> cardList;

    public CardAdapter(List<Card> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Card card = cardList.get(position);
        holder.textViewFront.setText(card.getText());
        holder.textViewBack.setText(card.getTranslation());

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
        return cardList.size();
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
}