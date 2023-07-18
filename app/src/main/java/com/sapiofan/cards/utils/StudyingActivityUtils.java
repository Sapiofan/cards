package com.sapiofan.cards.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.sapiofan.cards.entities.Collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudyingActivityUtils {

    public static void applyAnimation(final View visibleView, final View invisibleView) {
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

    public static String buildPath(List<Collection> collections, Collection collection, StringBuilder stringBuilder) {
        if(stringBuilder.length() == 0) {
            return buildPath(collections, collection, stringBuilder.append(collection.getName()));
        }

        if(collection.getParent() != 0) {
            Collection parent = collections.stream()
                    .filter(collection1 -> collection1.getId() == collection.getParent())
                    .collect(Collectors.toList()).get(0);
            return buildPath(collections, parent, stringBuilder.append(".").append(parent.getName()));
        }

        stringBuilder.append(".").append("root");

        String[] dividedPath = stringBuilder.toString().split("\\.");

        Collections.reverse(Arrays.asList(dividedPath));

        return String.join(".", dividedPath);
    }

    public static int getParentIdByPath(String path, Map<Collection, String> paths) {
        return paths.entrySet().stream()
                .filter(collectionStringEntry -> collectionStringEntry.getValue().equals(path))
                .findFirst().map(collectionStringEntry -> collectionStringEntry.getKey().getId())
                .orElse(0);
    }
}
