package com.example.jzhou.contextio;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class ContextActivity extends AppCompatActivity implements SensorEventListener{
    TextView acc_x_txt, acc_y_txt, acc_z_txt, acc_txt, mag_txt, light_txt, result_txt;
    SensorManager mSensorManager;
    Sensor acc_sensor, mag_sensor, light_sensor;
    public double light_val, mag_val1=0, mag_val2=0, mag_val3=0, acc, acc_val_x, acc_val_y, acc_val_z;
    AccuracyDbHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context);

        dbHelper = new AccuracyDbHelper(getApplicationContext());


        acc_x_txt = (TextView) findViewById(R.id.acc_x_value);
        acc_y_txt = (TextView) findViewById(R.id.acc_y_value);
        acc_z_txt = (TextView) findViewById(R.id.acc_z_value);
        acc_txt = (TextView) findViewById(R.id.acc_value);

        mag_txt = (TextView) findViewById(R.id.mag_value);
        light_txt = (TextView) findViewById(R.id.light_value);
        result_txt = (TextView) findViewById(R.id.result);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acc_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mag_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        light_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        deleteDb();
        copyDatabase();

        Log.d("DEBUGGING", "onCreate");
    }

    protected void insertDb(String location){
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AccuracyDbHelper.AccuracyENTRIES.column_name1, location);

        long newRowId = db.insert(AccuracyDbHelper.AccuracyENTRIES.TABLE_NAME, null, values);
        Log.d("DEBUGGING", "New Row Id: "+ newRowId);
    }

    protected void deleteDb(){
        db = dbHelper.getWritableDatabase();
        db.execSQL("delete from "+ AccuracyDbHelper.AccuracyENTRIES.TABLE_NAME);
    }


    private void copyDataBase1() throws IOException, PackageManager.NameNotFoundException {
        // Open your local db as the input stream
        PackageManager m = getPackageManager();
        String s = getPackageName();
        PackageInfo p = m.getPackageInfo(s, 0);
        s = p.applicationInfo.dataDir;
        Log.d("CopyDatabase", "package directory: "+ s);
        String dbname = s+"/Location.db.sqlite";
        Log.d("CopyDatabase", "getAssets" + getApplicationContext().getAssets());
        InputStream myInput = getApplicationContext().getAssets().open(dbname);
        Log.d("CopyDatabase", "myInput: "+myInput);
        // Path to the just created empty db
        File outFileName = getDatabasePath(dbname);
        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        Log.d("CopyDatabase", "myOutput: "+myOutput);
        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void copyDatabase() {
        String databasePath = getApplicationContext().getDatabasePath("Location.db").getPath();
        Log.d("CopyDatabase", "database path: "+ databasePath);
        File f = new File(databasePath);
        OutputStream myOutput = null;
        InputStream myInput = null;
        Log.d("testing", " testing db path " + databasePath);
        Log.d("testing", " testing db exist " + f.exists());

        if (f.exists()) {
            try {

                File directory = new File("/mnt/sdcard/DB_DEBUG");
                if (!directory.exists())
                    directory.mkdir();

                myOutput = new FileOutputStream(directory.getAbsolutePath()
                        + "/" + "Location.db");
                myInput = new FileInputStream(databasePath);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
            } catch (Exception e) {
            } finally {
                try {
                    if (myOutput != null) {
                        myOutput.close();
                        myOutput = null;
                    }
                    if (myInput != null) {
                        myInput.close();
                        myInput = null;
                    }
                } catch (Exception e) {
                }
            }
        }
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

            copyDatabase();

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
                acc_z_txt.setText(acc_z_result+" m/s²");

                acc = Math.sqrt(acc_val_x*acc_val_x + acc_val_y*acc_val_y + acc_val_z*acc_val_z);
                String acc_result = String.format("%.1f", acc);
                acc_txt.setText(acc_result+" m/s²");

                Log.d("DEBUGGING", "acc result = "+ acc_x_result);

                break;
        }

        int hour = 0;
        hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        Log.d("DEBUGGING", "hour of day: "+ hour);


        if (acc>11||acc<9){

            Log.d("DEBUGGING", "acc value: "+acc);
            if (Math.abs(mag_val3 - mag_val1)> 1) {
                result_txt.setText("Indoor");
                Log.d("DEBUGGING", "result is indoor");
                insertDb("Indoor");
            }
            else {
                if (10<hour&&hour<16){
                    Log.d("DEBUGGING", "hour of day: "+ hour);
                    if (light_val>200){
                        result_txt.setText("outdoor");
                        insertDb("Outdoor");
                    }
                    else{
                        result_txt.setText("semi-outdoor");
                        insertDb("Semi-Outdoor");
                    }
                    Log.d("DEBUGGING", "result is outdoor or semi-outdoor");
                }
                else{
                    Log.d("DEBUGGING", "hour of day: "+ hour);
                    result_txt.setText("outdoor or semi-outdoor");
                    insertDb("Outdoor or Semi-Outdoor");
                }

            }
        }

        Log.d("DEBUGGING", "onSensorChanged");
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

        Log.d("DEBUGGING", "onAccuracyChanged");
    }

}
