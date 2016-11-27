package com.example.jzhou.contextio;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.TextView;

public class ContextActivity extends AppCompatActivity implements SensorEventListener{
    TextView acc_x_txt, acc_y_txt, acc_z_txt, mag_txt, light_txt, result_txt;
    SensorManager mSensorManager;
    Sensor acc_sensor, mag_sensor, light_sensor;
    public double light_val, mag_val1=0, mag_val2=0, mag_val3=0, acc, acc_val_x, acc_val_y, acc_val_z;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context);

        acc_x_txt = (TextView) findViewById(R.id.acc_x_value);
        acc_y_txt = (TextView) findViewById(R.id.acc_y_value);
        acc_z_txt = (TextView) findViewById(R.id.acc_z_value);

        mag_txt = (TextView) findViewById(R.id.mag_value);
        light_txt = (TextView) findViewById(R.id.light_value);
        result_txt = (TextView) findViewById(R.id.result);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acc_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mag_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        light_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        Log.d("DEBUGGING", "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        Log.d("DEBUGGING", "onResume");
    }

    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(this);
        super.onStop();

        Log.d("DEBUGGING", "onStop");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        int sensorType = sensorEvent.sensor.getType();
        //StringBuilder sb = null;

        switch (sensorType)
        {
            case Sensor.TYPE_MAGNETIC_FIELD:
                //sb = new StringBuilder();
                mag_val1 = mag_val2;
                mag_val2 = mag_val3;
                mag_val3 = Math.sqrt(values[0]*values[0]+values[1]*values[1]+values[2]*values[2]);
                //sb.append(mag_val);
                String mag_result = String.format("%.1f", mag_val2);
                mag_txt.setText(mag_result+" uT");
                Log.d("DEBUGGING", "mag_val1 = "+ mag_val1);
                Log.d("DEBUGGING", "mag_val2 = "+ mag_val2);
                Log.d("DEBUGGING", "mag_val3 = "+ mag_val3);
                break;

            case Sensor.TYPE_LIGHT:
                //sb = new StringBuilder();
                light_val = values[0];
                //sb.append(light_val);
                String light_result = String.format("%.1f", light_val);
                Log.d("DEBUGGING", "light result1 = "+ light_val);
                light_txt.setText(light_result+" Lux");
                Log.d("DEBUGGING", "light result = "+ light_result);
                break;

            case Sensor.TYPE_ACCELEROMETER:
                //sb = new StringBuilder();
                acc_val_x = Math.sqrt(values[0]*values[0]);
                acc_val_y = Math.sqrt(values[1]*values[1]);
                acc_val_z = Math.sqrt(values[2]*values[2]);
                //sb.append(acc_val);
                String acc_x_result = String.format("%.1f", acc_val_x);
                acc_x_txt.setText(acc_x_result+" m/s²");

                String acc_y_result = String.format("%.1f", acc_val_y);
                acc_y_txt.setText(acc_y_result+" m/s²");

                String acc_z_result = String.format("%.1f", acc_val_z);

                acc = Math.sqrt(acc_val_x*acc_val_x + acc_val_y*acc_val_y + acc_val_z*acc_val_z);
                String acc_result = String.format("%.1f", acc);

                acc_z_txt.setText(acc_result+" m/s²");

                Log.d("DEBUGGING", "acc result = "+ acc_x_result);

                break;
        }


        if (acc>11||acc<9){
            Log.d("DEBUGGING", "acc value: "+acc);
            if (Math.abs(mag_val3 - mag_val1)> 1) {
                result_txt.setText("Indoor");
                Log.d("DEBUGGING", "result is indoor");
            }
            else {
                result_txt.setText("Outdoor or semi-outdoor");
                Log.d("DEBUGGING", "result is outdoor or semi-outdoor");
            }
        }

        Log.d("DEBUGGING", "onSensorChanged");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

        Log.d("DEBUGGING", "onAccuracyChanged");
    }

}
