package com.sapiofan.cards.entities;

import java.util.ArrayList;
import java.util.List;

public class Collection {
    private int id;
    private String name;
    private boolean inStudy;
    private List<Card> cards;
    private int parent;

    public Collection(int id, String name, boolean inStudy, int parent) {
        this.id = id;
        this.name = name;
        this.inStudy = inStudy;
        this.cards = new ArrayList<>();
        this.parent = parent;
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

    @Override
    public String toString() {
        return "Collection:\n name: " + name + "\ncards:" + cards
                + "\nparent: " + (parent == 0 ? "Root" : parent);
    }
}
