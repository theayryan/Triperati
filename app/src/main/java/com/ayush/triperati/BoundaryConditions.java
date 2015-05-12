package com.ayush.triperati;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by yushrox on 12-03-2015.
 */
public class BoundaryConditions {
    ArrayList<LatLng> latLng = new ArrayList<LatLng>();

    public BoundaryConditions(ArrayList<LatLng> latLng) {
        this.latLng = latLng;
    }

    public Double getMinimumLongitude() {
        Double min;
        min = latLng.get(0).longitude;
        for (int i = 1; i < latLng.size(); i++) {
            if (min > latLng.get(i).longitude)
                min = latLng.get(i).longitude;
            else
                continue;
        }
        return min;
    }

    public Double getMinimumLatitude() {
        Double min;
        min = latLng.get(0).latitude;
        for (int i = 1; i < latLng.size(); i++) {
            if (min > latLng.get(i).latitude)
                min = latLng.get(i).latitude;
            else
                continue;
        }
        return min;
    }

    public Double getMaximumLatitude() {
        Double max;
        max = latLng.get(0).latitude;
        for (int i = 1; i < latLng.size(); i++) {
            if (max < latLng.get(i).latitude)
                max = latLng.get(i).latitude;
            else
                continue;
        }
        return max;
    }

    public Double getMaximumLongitude() {
        Double max;
        max = latLng.get(0).longitude;
        for (int i = 1; i < latLng.size(); i++) {
            if (max < latLng.get(i).longitude)
                max = latLng.get(i).longitude;
            else
                continue;
        }
        return max;
    }

    public LatLng getMaximumLatlng() {
        LatLng latlng;
        latlng = new LatLng(getMaximumLatitude(), getMaximumLongitude());
        return latlng;
    }

    public LatLng getMinimumLatlng() {
        LatLng latlng;
        latlng = new LatLng(getMinimumLatitude(), getMinimumLongitude());
        return latlng;
    }

}
