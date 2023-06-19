package com.sapiofan.cards.adapters;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class CollectionDecoration extends RecyclerView.ItemDecoration {
    private final int spacing;

    public CollectionDecoration() {
        spacing = 48; // Set the desired spacing between the cards in pixels
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = spacing;
        outRect.right = spacing;
        outRect.bottom = spacing;
        outRect.top = spacing;
    }
}
