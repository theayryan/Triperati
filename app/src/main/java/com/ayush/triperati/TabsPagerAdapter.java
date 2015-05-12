package com.ayush.triperati;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by yushrox on 19-12-2014.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {


    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new UserFragment();
            case 2:
                return new MapFragment();
        }
        return null;

    }


    @Override
    public int getCount() {
        return 3;
    }
}
