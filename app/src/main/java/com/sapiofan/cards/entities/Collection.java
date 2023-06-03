package com.sapiofan.cards.entities;

import java.util.ArrayList;
import java.util.List;

public class Collection extends CollectionObject {
    private String name;
    private List<Card> cards;
    private Collection parent;

    public Collection(String name, Collection parent) {
        this.name = name;
        this.cards = new ArrayList<>();
        this.parent = parent;
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

    public Collection getParent() {
        return parent;
    }

    public void setParent(Collection parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "Collection:\n name: " + name + "\ncards:" + cards
                + "\nparent: " + (parent == null ? "Root" : parent.getName());
    }
}
