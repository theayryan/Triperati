package com.ayush.triperati;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.ayush.triperati.store.SharedPreferencesCredentialStore;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.styles.EditTextStyle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardView;
import it.gmariotti.cardslib.library.view.CardViewNative;
import mehdi.sakout.fancybuttons.FancyButton;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class MapFragment extends Fragment  {

    public int counter = 0;
    FragmentActivity fa;
    Design mtrlDesign = Design.MATERIAL_DARK;
    Delivery delivery;
    GoogleMap goog_map;
    Location loc;
    int count;

    twitter4j.Twitter twitter;
    SupportMapFragment fragment;
    ArrayList<LatLng> points;
    ArrayList<Points> pointsArrayList;
    CardViewNative cardView;
    Card card;
    float lastX;
    EditText JourneyBox;
    View this_fragment;
    FancyButton Send;
    Location current_location;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fa = (FragmentActivity) super.getActivity();
        this_fragment = inflater.inflate(R.layout.activity_map_fragment, container, false);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(fa);
        String[] tokens = new SharedPreferencesCredentialStore(prefs).read();
        AccessToken a = new AccessToken(tokens[0], tokens[1]);
        twitter = new TwitterFactory().getInstance();
        this.prefs = PreferenceManager.getDefaultSharedPreferences(fa);
        twitter.setOAuthConsumer(Constants.API_KEY, Constants.API_SECRET);
        twitter.setOAuthAccessToken(a);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mViewFlipper = (ViewFlipper) this_fragment.findViewById(R.id.viewFlipper);
        JourneyBox = (EditText) this_fragment.findViewById(R.id.JourneyBox);
        Send=(FancyButton) this_fragment.findViewById(R.id.send);
        Send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(JourneyBox.getText()!=null){
                    goog_map.clear();
                    new Card_builder().execute(JourneyBox.getText().toString());
                }
                else
                    Toast.makeText(fa,"No Text Found",Toast.LENGTH_SHORT).show();
            }
        });

        setHasOptionsMenu(true);
        return this_fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        points = new ArrayList<LatLng>();
        pointsArrayList = new ArrayList<Points>();

        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, fragment).commit();
        }


    }


    @Override
    public void onResume() {
        super.onResume();


        if (goog_map == null) {
            goog_map = fragment.getMap();
            //new Card_builder().execute();

        }
    }



    public void markerSwitch() {
        for (int i = 0; i < points.size(); i++) {
            if (i == counter) {

                goog_map.addMarker(new MarkerOptions()
                        .position(points.get(i))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                        .visible(true));
            } else
                goog_map.addMarker(new MarkerOptions().position(points.get(i)).visible(true));
        }
    }





    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        setHasOptionsMenu(true);

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = fa.getMenuInflater();
        inflate.inflate(R.menu.menu_map, menu);
        String[] check = new String[2];
        check = new SharedPreferencesCredentialStore(prefs).read();
        menu.getItem(0).setVisible(true);
        menu.getItem(0).setEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                delivery = PostOffice.newMail(fa)
                        .setTitle("Journey Tag")
                        .setIcon(R.drawable.ic_action_search)

                        .setThemeColor(
                                Color.CYAN)
                        .setDesign(mtrlDesign)
                        .showKeyboardOnDisplay(true)
                        .setButtonTextColor(Dialog.BUTTON_POSITIVE, R.color.blue_500)
                        .setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setStyle(new EditTextStyle.Builder(fa)
                                .setHint("Journey Tag:")
                                .setTextColor(Color.WHITE)
                                .setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE)

                                .setOnTextAcceptedListener(new EditTextStyle.OnTextAcceptedListener() {
                                    @Override
                                    public void onAccepted(String text) {
                                        new Card_builder().execute(text);
                                        //Toast.makeText(fa, "Text was accepted: " + text, Toast.LENGTH_SHORT).show();

                                    }
                                }).build())

                        .build();
                delivery.show(fa.getFragmentManager());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearCredentials() {
        Toast.makeText(fa, "Logging Out", Toast.LENGTH_SHORT).show();

        new SharedPreferencesCredentialStore(prefs).clearCredentials();
    }

    public class Card_builder extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("journey_data");
            query.whereContains("journey_tag", params[0]);
            query.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        if (parseObjects.size() > 0) {
                            for (int i = 0; i < parseObjects.size(); i++) {
                                ParseObject p = parseObjects.get(i);
                                final long tweet_id = p.getLong("tweet_id");
                                String journey_tag = p.getString("journey_tag");
                                ParseGeoPoint location = p.getParseGeoPoint("location");
                                Log.d("Parse_test", journey_tag + "  " + Long.toString(tweet_id) + " " + location);
                                if (location != null) {
                                    final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    points.add(latLng);
                                    try {
                                        card = new Card(fa);
                                        twitter4j.Status tweet = null;
                                        tweet = twitter.showStatus(tweet_id);
                                        CardHeader header = new CardHeader(fa);
                                        header.setTitle(tweet.getUser().getScreenName());
                                        card.setTitle(tweet.getText());
                                        card.addCardHeader(header);
                                        MyThumbnail pic = new MyThumbnail(fa, tweet.getUser().getBiggerProfileImageURL());
                                        pic.setExternalUsage(true);
                                        card.addCardThumbnail(pic);
                                        Points point = new Points();
                                        Essentials temp = new Essentials(tweet.getUser().getScreenName(),
                                                tweet.getText(),
                                                tweet.getUser().getBiggerProfileImageURL());
                                        point.setEssentials(temp);
                                        point.setCard(card);
                                        LayoutInflater inflater = (LayoutInflater) fa.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View view = inflater.inflate(R.layout.tweet_row, null);
                                        cardView = (CardViewNative) view.findViewById(R.id.carddemo_thumb_url1);
                                        cardView.setCard(card);
                                        mViewFlipper.addView(view);
                                        Log.d("Added View", "Hello");
                                        pointsArrayList.add(point);
                                        Log.d("point_added", "hello");
                                    } catch (TwitterException e1) {
                                        e1.printStackTrace();
                                    }


                                } else {
                                    Toast.makeText(fa, "Tweet Doesn't have location information", Toast.LENGTH_SHORT).show();
                                }

                            }
                            Route route = new Route();
                            for (int i = 0; i < points.size(); i++) {
                                if (i == counter) {
                                    goog_map.addMarker(new MarkerOptions()
                                            .position(points.get(i))
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                                            .visible(true));
                                } else
                                    goog_map.addMarker(new MarkerOptions().position(points.get(i)).visible(true));
                                if (i > 0)
                                    route.drawRoute(goog_map, fa, points.get(i - 1), points.get(i), false, "en");

                            }
                            BoundaryConditions boundaryConditions = new BoundaryConditions(points);
                            LatLngBounds latLngBounds = new LatLngBounds(boundaryConditions.getMinimumLatlng(),boundaryConditions.getMaximumLatlng());
                            goog_map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds.getCenter(), 10));

                        }
                        else
                            Toast.makeText(fa,"No Journey named "+JourneyBox.getText().toString(),Toast.LENGTH_SHORT).show();
                    }

                }

            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            FancyButton next = new FancyButton(fa);
            next = (FancyButton) fa.findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("Button click", "Next" + Integer.toString(pointsArrayList.size()));
                    mViewFlipper.showNext();

                    if (counter >= points.size() - 1)
                        counter = 0;
                    else
                        counter++;
                    markerSwitch();
                }
            });
            FancyButton previous = new FancyButton(fa);
            previous = (FancyButton) fa.findViewById(R.id.previous);
            previous.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("Button click", "Previous");
                    mViewFlipper.showPrevious();
                    if (counter <= 0)
                        counter = points.size() - 1;
                    else
                        counter--;
                    markerSwitch();
                }
            });

            mViewFlipper.setInAnimation(fa, android.R.anim.fade_in);
            mViewFlipper.setOutAnimation(fa, android.R.anim.fade_out);
            /*final GestureDetector gestureDetector;
            gestureDetector = new GestureDetector(
                    new MyGestureDetector());
                    mViewFlipper = (ViewFlipper) this_fragment.findViewById(R.id.viewFlipper);
                    mViewFlipper.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(final View view, final MotionEvent event) {
                        gestureDetector.onTouchEvent(event);
                        return true;
                    }
                });*/

        }

    }





}