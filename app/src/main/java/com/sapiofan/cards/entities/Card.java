package com.sapiofan.cards.entities;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Objects;

public class Card {
    private int id;
    private String text;
    private String translation;
    private Date nextRepetition;
    private int lastPeriod;
    private int collection_id;
    private boolean isSelected = false;

    private boolean isFlipped = false;

    public Card(int id, String text, String translation, Date nextRepetition, int lastPeriod, int collection_id) {
        this.id = id;
        this.text = text;
        this.translation = translation;
        this.nextRepetition = nextRepetition;
        this.lastPeriod = lastPeriod;
        this.collection_id = collection_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public Date getNextRepetition() {
        return nextRepetition;
    }

    public void setNextRepetition(Date nextRepetition) {
        this.nextRepetition = nextRepetition;
    }

    public int getLastPeriod() {
        return lastPeriod;
    }

    public void setLastPeriod(int lastPeriod) {
        this.lastPeriod = lastPeriod;
    }

    public int getCollection_id() {
        return collection_id;
    }

    public void setCollection_id(int collection_id) {
        this.collection_id = collection_id;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
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
        Card card = (Card) o;
        return id == card.id && collection_id == card.collection_id
                && text.equals(card.text) && translation.equals(card.translation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, translation, collection_id);
    }

    @NonNull
    @Override
    public String toString() {
        return "Card:\ntext: " + text + "\ntranslation: " + translation + "\nnext repetition: " + nextRepetition
                + "\nlast period: " + lastPeriod + "\ncollection: " + collection_id + "\n";
    }
}
