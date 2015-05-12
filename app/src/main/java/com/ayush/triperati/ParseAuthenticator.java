package com.ayush.triperati;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;

/**
 * Created by yushrox on 01-03-2015.
 */
public class ParseAuthenticator extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "7hVURVdSjcjxvr0W0hl8NrEQipmloWyCLCIRfjqK", "YEXOqJ8hTXKsi2l9ewImschboPwqcqLIxpevKT8j");

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }
}
