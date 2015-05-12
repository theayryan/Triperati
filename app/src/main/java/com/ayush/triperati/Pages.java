package com.ayush.triperati;

import java.util.ArrayList;

import twitter4j.ResponseList;
import twitter4j.Status;

/**
 * Created by yushrox on 27-01-2015.
 */
public class Pages {
    ArrayList<ArrayList<Status>> pages = new ArrayList<ArrayList<Status>>();
    ArrayList<Status> tweets = new ArrayList<Status>();

    ArrayList<Status> getPage(int page_number) {
        return pages.get(page_number);
    }

    int getNumberOfPages() {
        return pages.size();
    }

    Status getTweet(int page_number, int tweet_number) {
        ArrayList<Status> tweets = getPage(page_number);
        return tweets.get(tweet_number);
    }

    void setPage(ResponseList<Status> page) {
        ArrayList<Status> tweets = new ArrayList<Status>();
        for (Status status : page) {
            tweets.add(status);
        }
        pages.add(tweets);
    }

}
