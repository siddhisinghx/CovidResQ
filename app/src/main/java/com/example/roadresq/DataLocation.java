package com.example.roadresq;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class DataLocation {

    public static Location location = null;
    public static Boolean hasMarkerMoved = false;



    public static Boolean getHasMarkerMoved() {
        return hasMarkerMoved;
    }

    public static void setHasMarkerMoved(Boolean hasMarkerMoved) {
        DataLocation.hasMarkerMoved = hasMarkerMoved;
    }

    public static Location getLocation() {
        return location;

    }

    public static void setLocation(Location location, final Context context) {
        if(!( hasMarkerMoved))
            DataLocation.location = location;


//
//        location.getAccuracy()<8
    }




}