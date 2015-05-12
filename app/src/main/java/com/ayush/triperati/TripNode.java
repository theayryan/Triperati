package com.ayush.triperati;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.ArrayList;

/**
 * Created by yushrox on 12-05-2015.
 */
public class TripNode extends GenericJson {

    @Key
    String tripName;

    @Key
    Long tweetID;

    @Key("_geoloc")
    ArrayList<Double> tweetLocation;

    TripNode(){}

    TripNode(String tripName, Long tweetID, ArrayList<Double> tweetLocation){
        this.tripName=tripName;
        this.tweetID=tweetID;
        this.tweetLocation.addAll(tweetLocation);
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public void setTweetID(Long tweetID) {
        this.tweetID = tweetID;
    }

    public Long getTweetID() {
        return tweetID;
    }

    public void setTweetLocation(ArrayList<Double> tweetLocation) {
        this.tweetLocation = tweetLocation;
    }

    public ArrayList<Double> getTweetLocation() {
        return tweetLocation;
    }
}
