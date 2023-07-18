package com.sapiofan.cards.entities;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Collection {
    private int id;
    private String name;
    private boolean inStudy;
    private List<Card> cards;
    private int parent;
    private boolean isForCards;
    private boolean isSelected = false;

    public Collection(int id, String name, boolean inStudy, int parent, boolean isForCards) {
        this.id = id;
        this.name = name;
        this.inStudy = inStudy;
        this.cards = new ArrayList<>();
        this.parent = parent;
        this.isForCards = isForCards;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInStudy() {
        return inStudy;
    }

    public void setInStudy(boolean inStudy) {
        this.inStudy = inStudy;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public boolean isForCards() {
        return isForCards;
    }

    public void setForCards(boolean forCards) {
        isForCards = forCards;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collection that = (Collection) o;
        return id == that.id && parent == that.parent && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, parent);
    }

    @NonNull
    @Override
    public String toString() {
        return "Collection:\n name: " + name + "\ncards:" + cards
                + "\nparent: " + (parent == 0 ? "Root" : parent);
    }
}
