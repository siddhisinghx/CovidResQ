package com.example.roadresq;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class EmergencyActivity extends AppCompatActivity {
    int SECONDS=20;
    Handler handler;
    Runnable updateTask;
    SmoothProgressBar smoothProgressBar;
    Button buttonCancel;
    ArrayList<String> listHepers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        setHandler();
        initialize();
    }

    void  handleFirebase(){

        listHepers = new ArrayList<>();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("data").child(firebaseUser.getUid()).child("location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double latitude = Double.valueOf(dataSnapshot.child("lat").getValue().toString( ));
                Double longitude = Double.valueOf(dataSnapshot.child("long").getValue().toString());

                final Location own = new Location("own");
                own.setLatitude(latitude);
                own.setLongitude(longitude);

                DatabaseReference databaseReferenceOthers = FirebaseDatabase.getInstance().getReference();


                databaseReferenceOthers.child("all_locations").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Double othersLat = Double.valueOf(dataSnapshot.child("lat").getValue().toString());
                        Double othersLong = Double.valueOf(dataSnapshot.child("long").getValue().toString());

                        Location other = new Location("other");
                        other.setLatitude(othersLat);
                        other.setLongitude(othersLong);

                        float distance = own.distanceTo(other);
                        Log.i("abc", "ll: "+distance+" "+dataSnapshot.getKey());
                        if(distance<500 && !dataSnapshot.getKey().equals(firebaseUser.getUid())){
                            sendNotifications(dataSnapshot.getKey(),distance);
                        }




                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    void sendNotifications(String key, Float distance){
       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
       FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, String> map = new HashMap<>();
        map.put("distance", String.valueOf(distance));

       databaseReference.child("notifications").child(key).child(firebaseUser.getUid()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {

           }
       });
    }


    void initialize(){

        buttonCancel = findViewById(R.id.cancelButtonEmergency);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        smoothProgressBar = findViewById(R.id.progressBar);

    }

    void  setHandler(){

          handler=new Handler();

          updateTask=new Runnable() {
            @Override
            public void run() {
                updateTextView();
                if(SECONDS>=0)
                handler.postDelayed(this,1000);
                if(SECONDS==0){
                    finalUpdate();
                    handleFirebase();
                }
            }
        };

        handler.postDelayed(updateTask,1000);
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.M)
    void finalUpdate(){
        TextView textViewEmergency = findViewById(R.id.textAccidentDetected2);
        textViewEmergency.setText("Notifications sent!");
        //textViewEmergency.setTextColor(getColor(android.R.color.holo_green_dark));
        handler.removeCallbacks(updateTask);
        smoothProgressBar.setProgress(90,true);
        smoothProgressBar.setSmoothProgressDrawableColor(android.R.color.holo_green_dark);
        buttonCancel.setText("Call Ambulance");

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "102", null));
                startActivity(intent);
            }
        });
        Button revert = findViewById(R.id.revertButtonEmergency);
        revert.setVisibility(View.VISIBLE);
        revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revertFirebase();
            }
        });
    }

    void revertFirebase(){
        finish();
    }
    void updateTextView(){
        TextView textViewEmergency = findViewById(R.id.textAccidentDetected2);
        textViewEmergency.setText("We will now try to contact nearest cars for help in "+SECONDS--+" seconds...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateTask);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(updateTask);
    }
}
