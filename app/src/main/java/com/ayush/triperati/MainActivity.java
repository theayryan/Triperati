package com.ayush.triperati;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.ayush.triperati.store.CredentialStore;
import com.ayush.triperati.store.SharedPreferencesCredentialStore;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.styles.EditTextStyle;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import im.delight.android.location.SimpleLocation;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.GeoLocation;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class MainActivity extends FragmentActivity implements View.OnClickListener, DrawerLayout.DrawerListener {

    private static final int UPDATE_LOGIN = 1;
    SharedPreferences prefs;
    Delivery delivery;
    Design mtrlDesign = Design.MATERIAL_DARK;
    CommonsHttpOAuthConsumer consumer;
    OAuthProvider provider;
    Client kinveyclient;
    Typeface josefin;
    View this_fragment;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    //private ActionBar actionBar;
    private SimpleLocation simpleLocation;
    private boolean IS_TRIPS;
    private boolean IS_TWEETS;
    private DrawerLayout mDrawerLayout;
    private View mDrawerElementsContainer;
    private String profilePic;
    private String userName;
    private boolean IS_LOGGED_IN;
    private TextView mUserName;
    private ImageView mUserImage;
    private UiHandler uiHandler;
    private ServiceHandler mServiceHandler;
    private String[] tokens;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main_layout);
        kinveyclient = new Client.Builder(this).build();
        getActionBar().hide();
        this.consumer = new CommonsHttpOAuthConsumer(Constants.API_KEY, Constants.API_SECRET);
        this.provider = new DefaultOAuthProvider(Constants.REQUEST_URL, Constants.ACCESS_URL, Constants.AUTHORIZE_URL);
        simpleLocation = new SimpleLocation(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerElementsContainer = findViewById(R.id.drawer_root_layout);
        mUserImage = (ImageView) findViewById(R.id.user_image);
        mUserName = (TextView) findViewById(R.id.user_name);
        josefin = Typeface.createFromAsset(getAssets(), "fonts/JosefinSans-Regular.ttf");
        mUserName.setTypeface(josefin);
        viewPager = (ViewPager) findViewById(R.id.pager);

        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(mAdapter);
        PagerSlidingTabStrip slidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        slidingTabStrip.setViewPager(viewPager);
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        uiHandler = new UiHandler();
        String[] check = new String[2];
        HandlerThread mThread = new HandlerThread(this.getClass().getSimpleName(), android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mThread.start();
        mServiceHandler = new ServiceHandler(mThread.getLooper());
        check = new SharedPreferencesCredentialStore(prefs).read();
        if (check[0].isEmpty() && check[1].isEmpty()) {


        }
        /**
         * on swiping the viewpager make respective tab selected
         * */
        slidingTabStrip.setTextSize(50);
        slidingTabStrip.setAllCaps(true);
        slidingTabStrip.setIndicatorColor(getResources().getColor(R.color.appMain));
        slidingTabStrip.setIndicatorHeight(15);
        slidingTabStrip.setTextColor(getResources().getColor(R.color.appMainLight));
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/JosefinSans-Regular.ttf");
        slidingTabStrip.setTypeface(typeface,Typeface.BOLD);

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(Constants.OAUTH_CALLBACK_URL)) {
            new RetrieveAccessTokenTask(consumer, provider).execute(uri);
            Log.d("TAG", "called second async");
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
                        credentialStore.write(new String[]{accessToken, accessSecret});
                        Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "Successfully logged in via Twitter");
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleLocation.beginUpdates();
        tokens = new SharedPreferencesCredentialStore(prefs).read();
        if (!TextUtils.isEmpty(tokens[0])) {
            IS_LOGGED_IN = true;
            mServiceHandler.obtainMessage(UPDATE_LOGIN);
        } else {
            IS_LOGGED_IN = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        simpleLocation.endUpdates();
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
                new OAuthRequestTokenTask(this.getApplicationContext(), consumer, provider).execute();
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

    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void onDrawerSlide(View view, float v) {

    }

    public void onDrawerOpened(View view) {

    }

    public void onDrawerClosed(View view) {

    }

    public void onDrawerStateChanged(int i) {

    }

    private class UiHandler extends android.os.Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_LOGIN:
                    Transformation transformation = new RoundedTransformationBuilder()
                            .borderColor(Color.BLACK)
                            .borderWidthDp(3)
                            .cornerRadiusDp(30)
                            .oval(false)
                            .build();

                    Picasso.with(getApplicationContext())
                            .load(profilePic)
                            .fit()
                            .transform(transformation)
                            .into(mUserImage);
                    mUserName.setText("@" + userName);
            }

        }
    }

    private class OAuthRequestTokenTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private OAuthProvider provider;
        private CommonsHttpOAuthConsumer consumer;

        public OAuthRequestTokenTask(Context context, CommonsHttpOAuthConsumer consumer,
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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))/*.setFlags
                        (Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_NEW_TASK)*/;
                startActivity(intent);
            } catch (Exception e) {
                Log.e("TAG", "Error during OAUth retrieve request token", e);
            }
            return null;
        }
    }

    public class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, Void> {
        private OAuthProvider provider;
        private CommonsHttpOAuthConsumer consumer;

        public RetrieveAccessTokenTask(CommonsHttpOAuthConsumer consumer, OAuthProvider provider) {
            this.consumer = consumer;
            this.provider = provider;
        }

        @Override
        protected Void doInBackground(Uri... params) {
            final Uri uri = params[0];
            final String oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
            try {
                provider.retrieveAccessToken(consumer, oauth_verifier);
                loginTwitterKinveyUser(consumer.getToken(), consumer.getTokenSecret());
                Log.d("TAG", "OAuth - Access Token Retrieved");
            } catch (Exception e) {
                Log.d("TAG", "OAuth - Access Token Retrieval Error", e);
            }
            return null;
        }
    }

    public class TweetPoster extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            TwitterUtils twitterUtils = new TwitterUtils();
            try {
                GeoLocation geoLocation = new GeoLocation(simpleLocation.getLatitude(), simpleLocation.getLongitude());
                Log.d("current location", geoLocation.getLatitude() + " " + geoLocation.getLongitude());
                twitterUtils.sendTweet(prefs, params[0], geoLocation);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class ServiceHandler extends android.os.Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_LOGIN:
                    AccessToken a = new AccessToken(tokens[0], tokens[1]);
                    Twitter twitter = new TwitterFactory().getInstance();
                    twitter.setOAuthConsumer(Constants.API_KEY, Constants.API_SECRET);
                    twitter.setOAuthAccessToken(a);
                    try {
                        twitter4j.User user = twitter.showUser(twitter.getId());
                        profilePic = user.getBiggerProfileImageURL();
                        userName = user.getName();
                        uiHandler.obtainMessage(UPDATE_LOGIN);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
            }
        }
    }


}