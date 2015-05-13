package com.ayush.triperati;

import android.content.Context;
import android.widget.Toast;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.core.KinveyClientCallback;

import java.util.ArrayList;

/**
 * Created by yushrox on 12-05-2015.
 */
public class BackendHandler {
    Client kinveyClient;
    Context ctx;
    TripNode[] allTripNodes;

    BackendHandler(Context context) {
        ctx = context;
        kinveyClient = new Client.Builder(context).build();
        getAllTripNodes();
    }

    void addData(final String tripName, long tweetID, ArrayList<Double> tweetLocation) {
        TripNode tripNode = new TripNode();
        tripNode.setTripName(tripName);
        tripNode.setTweetID(tweetID);
        tripNode.setTweetLocation(tweetLocation);
        AsyncAppData<TripNode> newTripNode = kinveyClient.appData(Constants.NODE_COLLECTION, TripNode.class);
        newTripNode.save(tripNode, new KinveyClientCallback<TripNode>() {
            @Override
            public void onSuccess(TripNode tripNode) {
                Toast.makeText(ctx, "Added to trip " + tripName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(ctx, "Couldn't add please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void getAllTripNodes() {

        final AsyncAppData<TripNode> tripnodes = kinveyClient.appData(Constants.NODE_COLLECTION, TripNode.class);
        tripnodes.get(new KinveyListCallback<TripNode>() {
            @Override
            public void onSuccess(TripNode[] tripNodes) {
                allTripNodes = new TripNode[tripNodes.length];
                for (int i = 0; i < tripNodes.length; i++) {
                    allTripNodes[i] = tripNodes[i];
                }
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });

    }

    ArrayList<String> getAllTripTags() {
        ArrayList<String> allTripTags = new ArrayList<String>();
        if (allTripNodes != null) {

            allTripTags.add(allTripNodes[0].getTripName());
            for (int i = 1; i < allTripNodes.length; i++) {
                String tripTag = allTripNodes[i].getTripName();
                int flag = 0;
                for (int j = 0; j < i; j++) {
                    if (tripTag.equalsIgnoreCase(allTripTags.get(j)))
                        flag++;
                }
                if (flag == 0) {
                    allTripTags.add(tripTag);
                }
            }
        }
        return allTripTags;
    }

    ArrayList<String>[] getAllTripTagsWRTtweet(long tweetID){
        ArrayList<String>[] allTripTagsWRTtweet = new ArrayList[2];
        ArrayList<String> allTripTags = getAllTripTags();
        for(int i = 0;i<allTripTags.size();i++){
            String tag= new String();
            for(int j =0;j<allTripNodes.length;j++){
                if(allTripNodes[i].getTweetID()==tweetID){
                    tag=allTripNodes[i].getTripName();
                    break;
                }
            }
            if(tag!=null)
                allTripTagsWRTtweet[0].add(tag);
            else
                allTripTagsWRTtweet[1].add(tag);
        }
        return  allTripTagsWRTtweet;
    }
}