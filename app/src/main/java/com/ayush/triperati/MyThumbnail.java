package com.ayush.triperati;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import it.gmariotti.cardslib.library.internal.CardThumbnail;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by yushrox on 18-01-2015.
 */
public class MyThumbnail extends CardThumbnail {

    String myUrl;
    CropCircleTransformation transformation;

    public MyThumbnail(Context context, String myUrl) {
        super(context);
        this.myUrl = myUrl;
        transformation = new CropCircleTransformation();
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View viewImage) {

        //Here you have to set your image with an external library
        Picasso.with(getContext())
                .load(myUrl)
                .transform(transformation)
                .into((ImageView) viewImage);

        viewImage.getLayoutParams().width = 250;
        viewImage.getLayoutParams().height = 250;
    }
}
