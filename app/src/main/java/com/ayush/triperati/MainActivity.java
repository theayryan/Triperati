package com.ayush.triperati;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ayush.triperati.store.CredentialStore;
import com.ayush.triperati.store.SharedPreferencesCredentialStore;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationRequest;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.parse.ParseTwitterUtils;
import com.parse.signpost.OAuth;
import com.parse.signpost.OAuthProvider;
import com.parse.signpost.basic.DefaultOAuthProvider;
import com.parse.signpost.commonshttp.CommonsHttpOAuthConsumer;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.styles.EditTextStyle;

import twitter4j.GeoLocation;

public class MainActivity extends FragmentActivity implements
        ActionBar.TabListener,GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    SharedPreferences prefs;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    Delivery delivery;
    String provider1;
    LocationManager manager;
    Design mtrlDesign = Design.MATERIAL_DARK;
    // Tab titles
    //LocationClient mLocationClient;
    Location current_location;
    CommonsHttpOAuthConsumer consumer;
    OAuthProvider provider;
    Client kinveyclient;
    private String[] tabs = {"Home Timeline", "User Timeline", "Journey Map"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        kinveyclient = new Client.Builder(this.getApplicationContext()).build();
        this.consumer=new CommonsHttpOAuthConsumer(Constants.API_KEY,Constants.API_SECRET);
        this.provider=new DefaultOAuthProvider(Constants.REQUEST_URL,Constants.ACCESS_URL,Constants.AUTHORIZE_URL);

        // Initilization
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider1 = manager.getBestProvider(criteria, false);
        //loc = manager.getLastKnownLocation(provider);
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        manager.requestLocationUpdates(provider1, 1, 1, this);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        String[] check = new String[2];

        check = new SharedPreferencesCredentialStore(prefs).read();
        ParseTwitterUtils.initialize(Constants.API_KEY, Constants.API_SECRET);
        if(check[0].isEmpty() && check[1].isEmpty()){


        }
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }


            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }


            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    private class OAuthRequestTokenTask extends     AsyncTask<Void, Void, Void> {
        private Context    context;
        private OAuthProvider provider;
        private CommonsHttpOAuthConsumer consumer;
        public OAuthRequestTokenTask(Context context,CommonsHttpOAuthConsumer consumer,
                                     OAuthProvider provider) {
            this.context = context;
            this.consumer = consumer;
            this.provider = provider;
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                final String url =
                        provider.retrieveRequestToken(consumer, Constants.OAUTH_CALLBACK_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url))/*.setFlags
                        (Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_NEW_TASK)*/;
                startActivity(intent);
            } catch (Exception e) {
                Log.e("TAG", "Error during OAUth retrieve request token", e);
            }
            return null;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(Constants.OAUTH_CALLBACK_URL)) {
            new RetrieveAccessTokenTask(consumer,provider).execute(uri);
            Log.d("TAG","called second async");
        }
    }

    public class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, Void> {
        private OAuthProvider provider;
        private CommonsHttpOAuthConsumer consumer;
        public RetrieveAccessTokenTask(CommonsHttpOAuthConsumer consumer,OAuthProvider provider) {
            this.consumer = consumer;
            this.provider = provider;
        }
        @Override
        protected Void doInBackground(Uri...params) {
            final Uri uri = params[0];
            final String oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
            try {
                provider.retrieveAccessToken(consumer, oauth_verifier);
                loginTwitterKinveyUser(consumer.getToken(),consumer.getTokenSecret());
                Log.d("TAG", "OAuth - Access Token Retrieved");
            } catch (Exception e) {
                Log.d("TAG", "OAuth - Access Token Retrieval Error", e);
            }
            return null;
        }
    }
    private void loginTwitterKinveyUser(final String accessToken, final String accessSecret) {
        kinveyclient.user().loginTwitter(accessToken,
                accessSecret, Constants.API_KEY, Constants.API_SECRET, new KinveyUserCallback() {
                    @Override
                    public void onFailure(Throwable e) {
                        Log.e("TAG", "Failed Kinvey login", e);
                    }
                    @Override
                    public void onSuccess(User r) {
                        CredentialStore credentialStore = new SharedPreferencesCredentialStore(prefs);
                        credentialStore.write(new String[]{accessToken,accessSecret});
                        Toast.makeText(getApplicationContext(),"Logged in",Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "Successfully logged in via Twitter");
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }


    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }


    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.menu_main, menu);
        String[] check = new String[2];
        check = new SharedPreferencesCredentialStore(prefs).read();

        if (!check[0].isEmpty() || !check[1].isEmpty()) {
            menu.getItem(1).setVisible(true);
            menu.getItem(1).setEnabled(true);
            menu.getItem(0).setVisible(false);
            menu.getItem(0).setEnabled(false);
        } else {
            menu.getItem(1).setVisible(false);
            menu.getItem(1).setEnabled(false);
            menu.getItem(0).setVisible(true);
            menu.getItem(0).setEnabled(true);
            Toast.makeText(this.getApplicationContext(), "Please Login", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.twitter:
                delivery = PostOffice.newMail(this)
                        .setTitle("Tweet")
                        .setIcon(R.drawable.ic_action_tweet)

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
                        .setStyle(new EditTextStyle.Builder(this)
                                .setHint("Tweet here")
                                .setTextColor(Color.WHITE)
                                .setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE)

                                .setOnTextAcceptedListener(new EditTextStyle.OnTextAcceptedListener() {
                                    @Override
                                    public void onAccepted(String text) {
                                        new TweetPoster().execute(text);
                                        Toast.makeText(getApplicationContext(), "Text was accepted: " + text, Toast.LENGTH_SHORT).show();

                                    }
                                }).build())

                        .build();
                delivery.show(this.getFragmentManager());
                return true;
            case R.id.login:
                new OAuthRequestTokenTask(this.getApplicationContext(),consumer,provider).execute();
                return true;
            case R.id.logout:
                this.clearCredentials();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearCredentials() {
        Toast.makeText(this.getApplicationContext(), "Logging Out", Toast.LENGTH_SHORT).show();

        new SharedPreferencesCredentialStore(this.prefs).clearCredentials();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onLocationChanged(Location location) {
        current_location=location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public class TweetPoster extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            TwitterUtils twitterUtils = new TwitterUtils();
            try {
                Location location = manager.getLastKnownLocation(provider1);
                onLocationChanged(location);
                GeoLocation geoLocation = new GeoLocation(current_location.getLatitude(), current_location.getLongitude());
                Log.d("current location", geoLocation.getLatitude() + " " + geoLocation.getLongitude());
                twitterUtils.sendTweet(prefs,params[0],geoLocation);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}