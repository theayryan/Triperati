package com.ayush.triperati;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by yushrox on 15-01-2015.
 */
public class Essentials {
    String picture;
    String tweet;
    String handle_name;
    ArrayList<ArrayList<Card>> pages;

    public Essentials() {
        pages = new ArrayList<ArrayList<Card>>();
    }

    public Essentials(String handle_name, String tweet, String picture) {
        this.picture = picture;
        this.handle_name = handle_name;
        this.tweet = tweet;
    }

    public String getHandle_name() {
        return this.handle_name;
    }

    public void setHandle_name(String handle_name) {
        this.handle_name = handle_name;
    }

    public String getPicture() {
        return this.picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTweet() {
        return this.tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public void setPage(ArrayList<Card> page) {
        this.pages.add(page);
    }

    public int getNumberOfPages() {
        return this.pages.size();
    }

}

