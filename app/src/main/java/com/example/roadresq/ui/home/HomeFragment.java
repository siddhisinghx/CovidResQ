package com.example.roadresq.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.roadresq.DataLocation;
import com.example.roadresq.EmergencyActivity;
import com.example.roadresq.Extra;
import com.example.roadresq.NavigationDrawer;
import com.example.roadresq.R;
import com.example.roadresq.SliderAdapterExample;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements SensorEventListener {

    private HomeViewModel homeViewModel;
    View root;
    Handler handler;
    Runnable updateTask;

    Boolean GPS_SET = false;
    Boolean INTERNET_SET = false;


    SensorManager sensorManager;
    Sensor sensor;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    private float[] old_acceleration = new float[3];
    int cnt=0;

    Button button;

    ImageView gpsSet;
    ImageView internetSet;
    ImageView infoSet;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
         root = inflater.inflate(R.layout.fragment_home, container, false);
        initialize();
        return root;
    }

    void initialize(){

        button = root.findViewById(R.id.buttonStartSensors);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setBackground(getActivity().getDrawable(R.drawable.green_gradient));
                button.setText("You are all set!");
                function();
            }
        });


        gpsSet = root.findViewById(R.id.imageGPSStatus);
        internetSet = root.findViewById(R.id.imageInternetStatus);
        infoSet = root.findViewById(R.id.imageInfoStatus);

        setSlider();
        setHandler();



    }

    void setSlider(){

        SliderView sliderView = root.findViewById(R.id.imageSlider);
        SliderAdapterExample adapter = new SliderAdapterExample(getContext());
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(2); //set scroll delay in seconds :
        sliderView.startAutoCycle();
    }

    void  setHandler(){

        handler=new Handler();

        updateTask=new Runnable() {
            @Override
            public void run() {


                if (getActivity() != null)
                {

                    if (DataLocation.getLocation() != null) {
                        updateFirebaseLocation(DataLocation.getLocation());
                        if (!GPS_SET && DataLocation.getLocation().getAccuracy() < 100) {
                            GPS_SET = true;
                            gpsSet.setImageDrawable(getActivity().getDrawable(R.drawable.tick));

                        }
                    } else {
                        gpsSet.setImageDrawable(getActivity().getDrawable(R.drawable.cross));
                    }

                if (isNetworkConnected()) {
                    internetSet.setImageDrawable(getActivity().getDrawable(R.drawable.tick));
                } else {
                    internetSet.setImageDrawable(getActivity().getDrawable(R.drawable.cross));
                }
            }


                    handler.postDelayed(this,5000);

            }
        };

        handler.postDelayed(updateTask,1000);
    }


    void updateFirebaseLocation(Location location){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, String> map = new HashMap<>();
        map.put("lat",String.valueOf(location.getLatitude()));
        map.put("long",String.valueOf(location.getLongitude()));
        databaseReference.child("data").child(user).child("location").setValue(map);
        databaseReference.child("all_locations").child(user).setValue(map);

    }


    void function(){

        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {



        final float alpha = 0.8f;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];



        if(cnt==2){
            old_acceleration[0]= linear_acceleration[0];
            old_acceleration[1]= linear_acceleration[1];
            old_acceleration[2]= linear_acceleration[2];
        }
        if(cnt==7){

            float x = Math.abs(linear_acceleration[0] - old_acceleration[0]);
            float y = Math.abs(linear_acceleration[1] - old_acceleration[1]);
            float z = Math.abs(linear_acceleration[2] - old_acceleration[2]);
//            Log.i("abc", x+"    ");
//            Log.i("abc", y+"    ");
//            Log.i("abc", z+"    ");
//            Log.i("abc", " ------------------");

            if(x>9.9 || y>9.9 || z>9.9){
                sensorManager.unregisterListener(this,sensor);
                button.setBackground(getActivity().getDrawable(R.drawable.blue_gradient));
                button.setText("Tap to have a safe journey!");
                Intent intent = new Intent(getActivity(), EmergencyActivity.class);
                startActivity(intent);

            }

            cnt=0;

        }
        cnt++;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private boolean isNetworkConnected() {
        if(getActivity()!=null) {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
        }
        return true;
    }




}