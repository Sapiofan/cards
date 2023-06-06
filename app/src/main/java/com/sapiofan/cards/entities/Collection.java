package com.sapiofan.cards.entities;

import java.util.ArrayList;
import java.util.List;

public class Collection {
    private int id;
    private String name;
    private List<Card> cards;
    private int parent;

    public Collection(int id, String name, int parent) {
        this.id = id;
        this.name = name;
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
