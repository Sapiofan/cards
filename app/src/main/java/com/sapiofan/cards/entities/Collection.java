package com.sapiofan.cards.entities;

import java.util.ArrayList;
import java.util.List;

public class Collection {
    private String name;
    private List<Card> cards;

    public Collection(String name) {
        this.name = name;
        this.cards = new ArrayList<>();
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
}
