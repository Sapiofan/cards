package com.sapiofan.cards.entities;

import java.util.Date;

public class Card {
    private String text;
    private String translation;
    private Date nextRepetition;

    public Card(String text, String translation, Date nextRepetition) {
        this.text = text;
        this.translation = translation;
        this.nextRepetition = nextRepetition;
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
}
