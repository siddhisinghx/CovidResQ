package com.example.roadresq;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationGet extends AppCompatActivity implements LocationListener {

    private static FusedLocationProviderClient mFusedLocationClient;

    public NavigationDrawer activity;
    public GpsFetch gpsFetch;
    public static Boolean locationUpdated = false;

    public static Location mLocation;

    public static Location getmLocation() {
        return mLocation;
    }

    public static void setmLocation(Location mLocation) {
        LocationGet.mLocation = mLocation;
    }







    public LocationGet(Context context, NavigationDrawer activity) {


        this.activity = activity;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void getlocation2(Context context, final Activity activity) {


        locationUpdated = false;
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context
                , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }


        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    setmLocation(location);
                    DataLocation.setLocation(location,activity);
                    locationUpdated = true;
                } else {

                }

            }
        });




    }


    public void getlocation1() {



        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, (LocationListener) this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, (LocationListener) this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 100, 1, (LocationListener) this);

    }



    public void getlocation(){



        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(locationUpdated==false) {
                    getlocation1();
                    getlocation2(activity.getApplicationContext(),activity);
                    handler.postDelayed(this, 1000);

                }

                else {


                }
            }
        }, 2000);

    }





    @Override
    public void onLocationChanged(Location location) {
        locationUpdated=true;
        if(mLocation!=null ) {
            if (getmLocation().getAccuracy() > location.getAccuracy()) {
                setmLocation(location);
                DataLocation.setLocation(location,activity);

            }

            if(location.distanceTo(mLocation)>500){
                setmLocation(location);
                DataLocation.setLocation(location,activity);
            }
        }
        else if(mLocation==null){
            setmLocation(location);
            DataLocation.setLocation(location,activity);

        }



    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}