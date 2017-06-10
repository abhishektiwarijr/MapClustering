package com.example.administrator.mapclustering;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by administrator on 9/6/17.
 */

public class MyClusterItem implements ClusterItem {
    private final LatLng mPosition;
    private final String myTitle;
    private final String mySnippet;

    public MyClusterItem(double lat,double lng,String title,String snippet) {
        mPosition=new LatLng(lat,lng);
        myTitle=title;
        mySnippet = snippet;
    }
    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return myTitle;
    }

    @Override
    public String getSnippet() {
        return mySnippet;
    }
}
