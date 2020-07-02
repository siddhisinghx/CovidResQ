package com.example.roadresq;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Extra implements SensorEventListener {

    SensorManager sensorManager;
    Sensor sensor;
    Context context;
    Button button;

    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    private float[] old_acceleration = new float[3];
    int cnt=0;

    public Extra(Context context, Button button){

        this.button=button;
        this.context=context;
        function(context) ;



    }


    void function(Context context){

         sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
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
            Log.i("abc", x+"    ");
            Log.i("abc", y+"    ");
            Log.i("abc", z+"    ");
            Log.i("abc", " ------------------");

            if(x>9.9 || y>9.9 || z>9.9){
                sensorManager.unregisterListener(this,sensor);
                button.setBackground(context.getDrawable(R.color.cpb_grey));
            }

            cnt=0;

        }
        cnt++;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
