package com.ayush.triperati;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by ayushb on 22/5/15.
 */
public class CustomCard extends Card {

    String innertext;
    TextView innerTextView;
    Typeface typeface;

    public CustomCard(Context context) {
        super(context, R.layout.custom_card_layout);
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/PontanoSans-Regular.ttf");
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        this.innertext = title;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
        innerTextView = (TextView) parent.findViewById(R.id.card_main_inner_simple_title);
        innerTextView.setText(innertext);
        innerTextView.setTypeface(typeface);
    }
}
