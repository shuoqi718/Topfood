package edu.monash.topfood.maps;

import com.google.android.gms.maps.model.LatLng;

public class LocDetails {
    private String mLocName;
    private LatLng mLatLng;

    public LocDetails(String mLocName, LatLng mLatLng){
        this.mLatLng = mLatLng;
        this.mLocName = mLocName;
    }

    public String getmLocName() {
        return mLocName;
    }

    public void setmLocName(String mLocName) {
        this.mLocName = mLocName;
    }

    public LatLng getmLatLng() {
        return mLatLng;
    }

    public void setmLatLng(LatLng mLatLng) {
        this.mLatLng = mLatLng;
    }

}
