package com.ayush.triperati;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.styles.EditTextStyle;
import com.r0adkll.postoffice.styles.ListStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.gmariotti.cardslib.library.internal.CardExpand;
import mehdi.sakout.fancybuttons.FancyButton;
import twitter4j.GeoLocation;
import twitter4j.Status;

/**
 * Created by yushrox on 04-05-2015.
 */
public class CustomCardExpand extends CardExpand {
    Context ctx;
    FragmentManager fragmentManager;
    Status status;
    String dialogresult;
    public CustomCardExpand(Context context, int i, FragmentManager fragmentManager, Status status) {
        super(context, R.layout.card_expand_layout);
        ctx=context;
        this.fragmentManager=fragmentManager;
        this.status = status;
        dialogresult = new String ();
    }
    ArrayList<String> trips;
    Design mtrlDesign = Design.MATERIAL_DARK;
    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        trips = new ArrayList<String>();
        trips.add("Delhi");
        trips.add("Paris");
        TextView txtview= (TextView) parent.findViewById(R.id.list_item_tag_cloud);
        makeTagLinks("Delhi, Paris",txtview);
        FancyButton addButton = (FancyButton) parent.findViewById(R.id.addjourney);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makelistdialog();
            }
        });

    }

    private void makenewtripdialog(){
        Delivery delivery = PostOffice.newMail(ctx)
                .setTitle("New Trip Tag")
                .setThemeColor(Color.DKGRAY)
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
                .setStyle(new EditTextStyle.Builder(ctx)
                        .setHint("Tag Name")
                        .setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL)

                        .setOnTextAcceptedListener(new EditTextStyle.OnTextAcceptedListener() {
                            @Override
                            public void onAccepted(String text) {

                                //Toast.makeText(fa, "Text was accepted: " + text, Toast.LENGTH_SHORT).show();
                                dialogresult = new String();
                                dialogresult = text;
                                if(status.getGeoLocation()!=null)
                                    save_data(status.getId(), dialogresult, status.getGeoLocation());
                                else
                                    Toast.makeText(ctx,"Location information not available",Toast.LENGTH_SHORT).show();
                            }
                        }).build())

                .build();


        delivery.show(fragmentManager);
    }

    void save_data(final Long item_id, final String data, GeoLocation location) {
        ParseObject journey_data = new ParseObject("journey_data");
        journey_data.put("tweet_id", item_id);
        journey_data.put("journey_tag", data);
        ParseGeoPoint geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        journey_data.put("location", geoPoint);
        journey_data.saveInBackground();
        //Toast.makeText(fa, data + " " + Long.toString(item_id), Toast.LENGTH_LONG).show();
    }

    private void makelistdialog(){
        final CharSequence[] charSequence;
        charSequence = new CharSequence[trips.size()+1];
        for(int i = 0;i<trips.size()+1;i++){
            if(i==0){
                charSequence[i] = "Add to new trip";
            }
            else
                charSequence[i]= trips.get(i-1);
        }
        Delivery delivery = PostOffice.newSimpleListMail(ctx,"Add to trip",mtrlDesign,charSequence , new ListStyle.OnItemAcceptedListener<CharSequence>() {

            public void onItemAccepted(CharSequence s, int i) {
                if(i==0){
                    makenewtripdialog();
                }
                else{
                    if(status.getGeoLocation()!=null)
                        save_data(status.getId(), s.toString(), status.getGeoLocation());
                    else
                        Toast.makeText(ctx,"Location information not available",Toast.LENGTH_SHORT).show();
                }
            }
        });
        delivery.show(fragmentManager);
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
        		tv.setText(ss, TextView.BufferType.SPANNABLE);
        	}


private class MyClickableSpan extends ClickableSpan {
    private final String mText;
    private MyClickableSpan(final String text) {
        mText = text;
    }
    @Override
    public void onClick(final View widget) {
        Toast.makeText(ctx,mText,Toast.LENGTH_SHORT).show();
    }
}
}