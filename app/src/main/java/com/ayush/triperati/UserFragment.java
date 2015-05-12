package com.ayush.triperati;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ayush.triperati.store.SharedPreferencesCredentialStore;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardListView;
import twitter4j.GeoLocation;
import twitter4j.ResponseList;
import twitter4j.Status;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class UserFragment extends Fragment implements OnRefreshListener {
    static int flag = 0;
    public String dialogresult;
    PullToRefreshLayout pullrefresh;
    Dialog dialog;
    CardArrayAdapter adapter_card;
    CardListView card_listview;
    Status status_final;
    View ll;
    FragmentActivity fa;
    Delivery delivery;
    Design mtrlDesign = Design.MATERIAL_DARK;
    ArrayList<Essentials> timeline;
    int current_index;
    int current_page;
    Pages page;
    Essentials tweet_pages;
    ViewToClickToExpand viewToClickToExpand;
    private SharedPreferences prefs;
    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        fa = (FragmentActivity) super.getActivity();
        ll = (LinearLayout) inflater.inflate(R.layout.activity_home_fragment, container, false);
        pullrefresh = (PullToRefreshLayout) ll.findViewById(R.id.pullrefresh);

        this.prefs = PreferenceManager.getDefaultSharedPreferences(fa);
        dialog = new Dialog(fa);
        current_page = 1;
        ActionBarPullToRefresh.from(fa).allChildrenArePullable().listener(this).setup(pullrefresh);
        textView = (TextView) ll.findViewById(R.id.response_code);
        timeline = new ArrayList<Essentials>();
        card_listview = (CardListView) ll.findViewById(R.id.myList);
        tweet_pages = new Essentials();
        String[] check = new String[2];
        viewToClickToExpand = ViewToClickToExpand.builder()
                .useLongClick(true)
                .highlightView(false)
                .setupCardElement(ViewToClickToExpand.CardElementUI.CARD);
        check = new SharedPreferencesCredentialStore(prefs).read();
        if (!check[0].isEmpty() && !check[1].isEmpty()) {
            pullrefresh.setRefreshing(true);
            onRefreshStarted(ll);
        }
        return ll;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRetainInstance(true);
        setHasOptionsMenu(true);

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    private void clearCredentials() {
        new SharedPreferencesCredentialStore(prefs).clearCredentials();
    }

    /**
     * Performs an authorized API call.
     */
    void save_data(final Long item_id, final String data, GeoLocation location) {
        ParseObject journey_data = new ParseObject("journey_data");
        journey_data.put("tweet_id", item_id);
        journey_data.put("journey_tag", data);
        ParseGeoPoint geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        journey_data.put("location", geoPoint);
        journey_data.saveInBackground();
        //Toast.makeText(fa, data + " " + Long.toString(item_id), Toast.LENGTH_LONG).show();
    }

    public void onRefreshStarted(View view) {

        if (adapter_card != null) {
            timeline.clear();
            tweet_pages.pages.clear();
            adapter_card.clear();
            flag++;
            current_page = 1;
        }
        new list_refresh().execute();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = fa.getMenuInflater();
        inflate.inflate(R.menu.menu_main, menu);
        String[] check = new String[2];
        //menu.getItem(3).setVisible(false);
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
            Toast.makeText(fa, "Please Login", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:

                return true;
            case R.id.logout:
                this.clearCredentials();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class list_refresh extends AsyncTask<Uri, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Uri... params) {
            ArrayList<Card> cards = new ArrayList<Card>();
            try {
                ResponseList<twitter4j.Status> userTimeline = TwitterUtils.getUserTimeline(prefs, current_page);
                Log.d("Page Count: ", Integer.toString(current_page));
                for (final twitter4j.Status status : userTimeline) {
                    Essentials tweet_data = new Essentials(status.getUser().getScreenName(), status.getText(), status.getUser().getBiggerProfileImageURL());

                    Log.d("tweet", status.getCreatedAt() + " " + status.getUser().getName() + " " + status.getText());
                    timeline.add(tweet_data);
                    Card sample = new Card(fa);
                    CardHeader header = new CardHeader(fa);
                    header.setTitle(tweet_data.getHandle_name());
                    sample.setTitle(tweet_data.getTweet());
                    CustomCardExpand customCardExpand = new CustomCardExpand(fa, 10, fa.getFragmentManager(), status);
                    //customCardExpand.setTitle("Trips");
                    sample.addCardExpand(customCardExpand);
                    sample.addCardHeader(header);
                    MyThumbnail pic = new MyThumbnail(fa, tweet_data.getPicture());
                    pic.setExternalUsage(true);
                    sample.addCardThumbnail(pic);
                    sample.setViewToClickToExpand(viewToClickToExpand);
                    cards.add(sample);
                }
                tweet_pages.setPage(cards);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (current_page == 1) {
                if (flag == 0) {
                    adapter_card = new CardArrayAdapter(fa, tweet_pages.pages.get(current_page - 1));
                    card_listview.setAdapter(adapter_card);
                } else {
                    adapter_card.addAll(tweet_pages.pages.get(current_page - 1));
                    adapter_card.notifyDataSetChanged();
                }
            } else {
                adapter_card.addAll(tweet_pages.pages.get(current_page - 1));
                adapter_card.notifyDataSetChanged();
            }
            current_page++;
            card_listview.setOnScrollListener(new EndlessScrollListener());
            pullrefresh.setRefreshComplete();


        }
    }

    public class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 20;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // I load the next page of gigs using a background task,
                // but you can call any function here.

                new list_refresh().execute();
                //current_page++;
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

}
