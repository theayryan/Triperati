package com.ayush.triperati;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardViewNative;

/**
 * Created by yushrox on 04-05-2015.
 */
public class CustomListAdapter extends ArrayAdapter<Card> {
    Context context;


    public CustomListAdapter(Context context, ArrayList<Card> cards) {

        super(context, 0, cards);
        this.context = context;
    }



    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position

        Card card = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, parent, false);

        }

        // Lookup view for data population
        CardViewNative cardViewNative =(CardViewNative) convertView.findViewById(R.id.listcard);
        cardViewNative.setCard(card);
        // Return the completed view to render on screen

        return convertView;

    }

}