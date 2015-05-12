package com.ayush.triperati;

import android.content.SharedPreferences;
import android.util.Log;

import com.ayush.triperati.store.SharedPreferencesCredentialStore;

import twitter4j.GeoLocation;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterUtils {

    public static boolean isAuthenticated(SharedPreferences prefs) {

        String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
        AccessToken a = new AccessToken(tokens[0], tokens[1]);
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(Constants.API_KEY, Constants.API_SECRET);
        twitter.setOAuthAccessToken(a);

        try {
            twitter.getAccountSettings();
            return true;
        } catch (TwitterException e) {
            return false;
        }
    }

    public static ResponseList<Status> getUserTimeline(SharedPreferences prefs) throws Exception {
        String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
        AccessToken a = new AccessToken(tokens[0], tokens[1]);
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(Constants.API_KEY, Constants.API_SECRET);
        twitter.setOAuthAccessToken(a);
        Paging page = new Paging(1, 50);
        ResponseList<Status> userTimeline = twitter.getUserTimeline(page);
        return userTimeline;
    }

    public static ResponseList<Status> getHomeTimeline(SharedPreferences prefs) throws Exception {
        String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
        AccessToken a = new AccessToken(tokens[0], tokens[1]);
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(Constants.API_KEY, Constants.API_SECRET);
        twitter.setOAuthAccessToken(a);
        Paging page = new Paging(1, 50);
        ResponseList<Status> homeTimeline = twitter.getHomeTimeline(page);
        return homeTimeline;
    }

    public static ResponseList<Status> getUserTimeline(SharedPreferences prefs, int page_count) throws Exception {
        String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
        AccessToken a = new AccessToken(tokens[0], tokens[1]);
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(Constants.API_KEY, Constants.API_SECRET);
        twitter.setOAuthAccessToken(a);
        Paging page = new Paging(page_count, 50);
        ResponseList<Status> userTimeline = twitter.getUserTimeline(page);
        return userTimeline;
    }

    public static ResponseList<Status> getHomeTimeline(SharedPreferences prefs, int page_count) throws Exception {
        String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
        AccessToken a = new AccessToken(tokens[0], tokens[1]);
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(Constants.API_KEY, Constants.API_SECRET);
        twitter.setOAuthAccessToken(a);
        Paging page = new Paging(page_count, 50);
        ResponseList<Status> homeTimeline = twitter.getHomeTimeline(page);
        return homeTimeline;
    }

    public static void sendTweet(SharedPreferences prefs, String msg, GeoLocation location) throws Exception {
        String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
        AccessToken a = new AccessToken(tokens[0], tokens[1]);
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(Constants.API_KEY, Constants.API_SECRET);
        twitter.setOAuthAccessToken(a);
        StatusUpdate statusUpdate = new StatusUpdate(msg);
        statusUpdate.setLocation(location);
        statusUpdate.displayCoordinates(true);
        Log.d("Location in the tweet", location.getLatitude() + " " + location.getLongitude());
        Status status = twitter.updateStatus(statusUpdate);
        Log.d("STATUS LOCATION", status.getText() + " " + status.getGeoLocation());

    }
}
