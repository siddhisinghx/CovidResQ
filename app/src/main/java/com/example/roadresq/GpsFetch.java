package com.example.roadresq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class GpsFetch extends Activity {

    protected static final int REQUEST_LOCATION = 0x1;
    protected static final int FORECE_REQUEST_LOCATION = 0x2;
    GoogleApiClient googleApiClient;
    Context context;
    MainActivity mainActivity;
    LocationGet locationGet;



    public GpsFetch(Context context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity=mainActivity;
    }

    public  void statusCheck() {


//        locationGet=new LocationGet(context,mainActivity);

        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            buildAlertMessageNoGps();

            enableLoc();

        }
        else {

        }



    }




    private void enableLoc() {




        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5 * 1000);
            locationRequest.setSmallestDisplacement(5);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().

                                status.startResolutionForResult(mainActivity, FORECE_REQUEST_LOCATION);

//                                finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int isSuccess=0;
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case FORECE_REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made

//                        locationGet.getlocation(context,mainActivity);//FINALLY YOUR OWN METHOD TO GET YOUR USER LOCATION HERE
                        locationGet.getlocation();
                        break;
                    case Activity.RESULT_CANCELED:


                        Toast.makeText(mainActivity.getApplicationContext(),"GPS is required, please turn it on.",Toast.LENGTH_LONG).show();
                        // The user was asked to change settings, but chose not to

                        break;
                    default:
                        break;
                }
                break;


        }
    }



}
