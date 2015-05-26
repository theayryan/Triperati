package com.ayush.triperati;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by ayushb on 24/5/15.
 */
public class CustomCardHeader extends CardHeader {

    TextView headerTextView;
    String headerText;
    Typeface typeface;

    public CustomCardHeader(Context context) {
        super(context, R.layout.custom_card_header);
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/JosefinSans-Regular.ttf");
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        this.headerText = title;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
        headerTextView = (TextView) parent.findViewById(R.id.card_header_text);
        headerTextView.setTypeface(typeface);
        headerTextView.setText("@" + headerText);
    }
}
