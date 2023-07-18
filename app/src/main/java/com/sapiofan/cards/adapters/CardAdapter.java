package com.sapiofan.cards.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.Log;
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
import com.sapiofan.cards.services.CardTableHandler;
import com.sapiofan.cards.services.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private List<Card> cardList;
    private final List<Card> filteredCardList;
    private final CardWord cardWord;
    private final CardTableHandler cardTableHandler;
    private OnSelectionModeCardChangeListener selectionModeChangeListener;
    private int currentFolderId;
    private boolean selectionMode = false;

    public CardAdapter(List<Card> cardList, CardWord cardWord, CardTableHandler cardTableHandler) {
        this.cardList = cardList;
        this.filteredCardList = new ArrayList<>(cardList);
        this.cardWord = cardWord;
        this.cardTableHandler = cardTableHandler;
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
        if (card == null) {
            Log.e("Empty card", "Card is null");
            return;
        }
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

        holder.itemView.setSelected(card.isSelected());
    }

    @Override
    public int getItemCount() {
        return filteredCardList.size();
    }

    public void addCard(String text1, String text2, boolean reverse) {
        cardTableHandler.addCard(text1, text2, currentFolderId, reverse);
        cardList.addAll(cardTableHandler.findCards(text1, text2, currentFolderId));
        filter("");
        notifyDataSetChanged();
    }

    public void removeCards(List<Card> selectedCards) {
        selectedCards.forEach(selectedCard -> cardTableHandler.removeCardById(selectedCard.getId()));
        cardList.removeAll(selectedCards);
        filter("");
        notifyDataSetChanged();
    }

    public int getCurrentFolderId() {
        return currentFolderId;
    }

    public void setCurrentFolderId(int currentFolderId) {
        this.currentFolderId = currentFolderId;
    }

    public List<Card> getSelectedCards() {
        return filteredCardList.stream().filter(Card::isSelected).collect(Collectors.toList());
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }

    public void setOnSelectionModeChangeListener(OnSelectionModeCardChangeListener listener) {
        this.selectionModeChangeListener = listener;
    }

    public void filter(String query) {
        query = query.toLowerCase().trim();

        filteredCardList.clear();

        if (query.isEmpty()) {
            filteredCardList.addAll(cardList);
        } else {
            for (Card card : cardList) {
                if (card.getText().toLowerCase().contains(query)) {
                    filteredCardList.add(card);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void setCards(List<Card> cards) {
        this.cardList = cards;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
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
                    if (!selectionMode) {
                        if (isFrontVisible) {
                            applyAnimation(textViewFront, textViewBack);
                        } else {
                            applyAnimation(textViewBack, textViewFront);
                        }
                        isFrontVisible = !isFrontVisible;
                    } else {
                        card.setSelected(!card.isSelected());
                        itemView.setSelected(card.isSelected());
                        if (!card.isSelected()) {
                            if (selectionModeChangeListener != null && getSelectedCards().size() == 0) {
                                selectionModeChangeListener.onSelectionCardModeChanged(false);
                            }
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Card card = filteredCardList.get(position);
                card.setSelected(!card.isSelected());
                itemView.setSelected(card.isSelected());
                boolean selectionMode = isAnyCardSelected();
                setSelectionMode(selectionMode);

                if (selectionModeChangeListener != null) {
                    selectionModeChangeListener.onSelectionCardModeChanged(selectionMode);
                }
                notifyItemChanged(position);
                return true;
            }
            return false;
        }

        private boolean isAnyCardSelected() {
            return filteredCardList.stream().anyMatch(Card::isSelected);
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

    public interface OnSelectionModeCardChangeListener {
        void onSelectionCardModeChanged(boolean selectionMode);
    }
}