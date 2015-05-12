package com.ayush.triperati;

import com.google.android.gms.maps.model.Marker;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by yushrox on 02-03-2015.
 */
public class Points {
    Marker selected;
    Marker unselected;
    Essentials essentials;
    Card card;
    long tweet;

    public long getTweet() {
        return tweet;
    }

    public void setTweet(long tweet) {
        this.tweet = tweet;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Marker getSelected() {
        return selected;
    }

    public void setSelected(Marker selected) {
        this.selected = selected;
    }

    public Marker getUnselected() {
        return unselected;
    }

    public void setUnselected(Marker unselected) {
        this.unselected = unselected;
    }

    public Essentials getEssentials() {
        return essentials;
    }

    public void setEssentials(Essentials essentials) {
        this.essentials = essentials;
    }
}
