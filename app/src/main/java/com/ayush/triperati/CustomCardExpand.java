package com.ayush.triperati;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputType;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.IconTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.styles.EditTextStyle;
import com.r0adkll.postoffice.styles.ListStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.gmariotti.cardslib.library.internal.CardExpand;
import twitter4j.Status;

/**
 * Created by yushrox on 04-05-2015.
 */
public class CustomCardExpand extends CardExpand {
    Context ctx;
    FragmentManager fragmentManager;
    Status status;
    String dialogresult;
    ArrayList<String> trips;
    Design mtrlDesign = Design.MATERIAL_DARK;
    Typeface josefin;
    Typeface pontano;
    TextView innerTitle;
    ArrayList<String>[] allTripTagsWRTtweet;
    BackendHandler backendHandler;
    public CustomCardExpand(Context context, int i, FragmentManager fragmentManager, Status status) {
        super(context, R.layout.card_expand_layout);
        ctx = context;
        this.fragmentManager = fragmentManager;
        this.status = status;
        dialogresult = new String();
        josefin = Typeface.createFromAsset(context.getAssets(), "fonts/JosefinSans-Regular.ttf");
        pontano = Typeface.createFromAsset(context.getAssets(), "fonts/PontanoSans-Regular.ttf");
        this.backendHandler = new BackendHandler(context);
        allTripTagsWRTtweet = backendHandler.getAllTripTagsWRTtweet(status.getId());
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        StringBuffer tags=new StringBuffer();
        if(allTripTagsWRTtweet[0]!=null) {
            trips = allTripTagsWRTtweet[0];
            for(int i = 0; i< trips.size();i++){
                tags.append(trips.get(i)+", ");
            }
        }
        else
            tags.append(ctx.getString(R.string.no_trips));
        String tagString = tags.toString();
        innerTitle = (TextView) parent.findViewById(R.id.card_expand_inner_simple_title);
        innerTitle.setText("Trips");
        innerTitle.setTypeface(josefin);
        TextView txtview = (TextView) parent.findViewById(R.id.list_item_tag_cloud);
        makeTagLinks(tagString, txtview);
        IconTextView addButton = (IconTextView) parent.findViewById(R.id.addJourney);
        addButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    makelistdialog();
                }
            });

    }

    private void makenewtripdialog() {
        Delivery delivery = PostOffice.newMail(ctx)
                .setTitle(ctx.getString(R.string.new_trip_tag))
                .setThemeColor(Color.DKGRAY)
                .setDesign(mtrlDesign)
                .showKeyboardOnDisplay(true)
                .setButtonTextColor(Dialog.BUTTON_POSITIVE, R.color.blue_500)
                .setButton(Dialog.BUTTON_POSITIVE, ctx.getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setButton(Dialog.BUTTON_NEGATIVE, ctx.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setStyle(new EditTextStyle.Builder(ctx)
                        .setHint(ctx.getResources().getString(R.string.tag_name))
                        .setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL)

                        .setOnTextAcceptedListener(new EditTextStyle.OnTextAcceptedListener() {
                            @Override
                            public void onAccepted(String text) {

                                //Toast.makeText(fa, "Text was accepted: " + text, Toast.LENGTH_SHORT).show();
                                dialogresult = new String();
                                dialogresult = text;
                                if (status.getGeoLocation() != null) {
                                    ArrayList<Double> location = new ArrayList<Double>();
                                    location.add(status.getGeoLocation().getLongitude());
                                    location.add(status.getGeoLocation().getLatitude());
                                    Log.d("Location", location.get(0) + " " + location.get(1));
                                    backendHandler.addData(dialogresult, status.getId(), location);
                                } else
                                    Toast.makeText(ctx, ctx.getResources().getText(R.string.location_not_available), Toast.LENGTH_SHORT).show();
                            }
                        }).build())

                .build();


        delivery.show(fragmentManager);
    }


    private void makelistdialog() {
        ArrayList<String> tripsPossible = allTripTagsWRTtweet[1];
        final CharSequence[] charSequence;
        if(tripsPossible!=null) {

            charSequence = new CharSequence[tripsPossible.size() + 1];
            for (int i = 0; i < trips.size() + 1; i++) {
                if (i == 0) {
                    charSequence[i] = ctx.getResources().getString(R.string.add_to_new_trip);
                } else
                    charSequence[i] = trips.get(i - 1);
            }

            Delivery delivery = PostOffice.newSimpleListMail(ctx, ctx.getString(R.string.add_to_trip), mtrlDesign, charSequence, new ListStyle.OnItemAcceptedListener<CharSequence>() {

                public void onItemAccepted(CharSequence s, int i) {
                    if (i == 0) {
                        makenewtripdialog();
                    } else {
                        if (status.getGeoLocation() != null) {
                            ArrayList<Double> location = new ArrayList<Double>();
                            location.add(status.getGeoLocation().getLongitude());
                            location.add(status.getGeoLocation().getLatitude());
                            Log.d("Location", location.get(0) + " " + location.get(1));
                            backendHandler.addData(s.toString(), status.getId(), location);
                        }
                        else
                            Toast.makeText(ctx, ctx.getString(R.string.location_not_available), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            delivery.show(fragmentManager);
        }
        else{
            Toast.makeText(ctx, ctx.getString(R.string.part_of_all_trips), Toast.LENGTH_SHORT).show();
            makenewtripdialog();
        }
    }

    private void makeTagLinks(final String text, final TextView tv) {
        if (text == null || tv == null) {
            return;
        }
        final SpannableString ss = new SpannableString(text);
        final List<String> items = Arrays.asList(text.split("\\s*,\\s*"));
        int start = 0, end;
        for (final String item : items) {
            end = start + item.length();
            if (start < end) {
                ss.setSpan(new MyClickableSpan(item), start, end, 0);
            }
            start += item.length() + 2;//comma and space in the original text ;)
        }
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setTypeface(pontano);
        tv.setText(ss, TextView.BufferType.SPANNABLE);
    }


    private class MyClickableSpan extends ClickableSpan {
        private final String mText;

        private MyClickableSpan(final String text) {
            mText = text;
        }

        @Override
        public void onClick(final View widget) {
            Toast.makeText(ctx, mText, Toast.LENGTH_SHORT).show();
        }
    }

}