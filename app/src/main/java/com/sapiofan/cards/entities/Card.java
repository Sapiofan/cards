package com.sapiofan.cards.entities;

import java.util.Date;

public class Card {
    private int id;
    private String text;
    private String translation;
    private Date nextRepetition;
    private int lastPeriod = 0;
    private int collection_id;

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

    @Override
    public String toString() {
        return "Card:\ntext: " + text + "\ntranslation: " + translation + "\nnext repetition: " + nextRepetition
                + "\nlast period: " + lastPeriod + "\ncollection: " + collection_id + "\n";
    }
}
