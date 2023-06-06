package com.sapiofan.cards.adapters;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class FlipAnimation extends Animation {
    private View view;
    private float centerX;
    private float centerY;
    private boolean forward = true;

    public FlipAnimation(View view) {
        this.view = view;
        setDuration(500);
        setFillAfter(false);
        setInterpolator(new AccelerateDecelerateInterpolator());
    }

    public void reverse() {
        forward = !forward;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        centerX = width / 2f;
        centerY = height / 2f;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float degrees = 180f * interpolatedTime;
        if (forward) {
            view.setRotationY(degrees);
        } else {
            view.setRotationY(-degrees);
        }
        view.setAlpha(interpolatedTime < 0.5f ? 1 - interpolatedTime : interpolatedTime);
    }
}
