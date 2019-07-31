package com.example.sensorlogging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;

import static java.time.Instant.now;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    //Calibrated magno stuff
    private TextView t_x, t_y, t_z;
    private float magX, magY, magZ;
    //Non Calibrated Magno stuff
    private TextView t_ncx, t_ncy, t_ncz;
    private float ncMagX, ncMagY, ncMagZ;
    //Gyroscope
    private TextView t_gX, t_gY, t_gZ;
    private float gX, gY, gZ;
    //Gyroscope -uncalibrated
    private TextView t_ugX, t_ugY, t_ugZ;
    private float ugX, ugY, ugZ;

    //Log writing


    File externalStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    String FILENAME = "sensorLog.csv";
    File outputlog = new File(externalStorageDirectory, FILENAME);
    float[] magLog = new float[4];
    float[] ucMagLog = new float[4];
    float[] ucGyroLog = new float[4];
    float[] gyroLog = new float[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //calibrated magno
        t_x = findViewById(R.id.magX);
        t_y = findViewById(R.id.magY);
        t_z = findViewById(R.id.magZ);
        //non Calibrated magno
        t_ncx = findViewById(R.id.ncMagX);
        t_ncy = findViewById(R.id.ncMagY);
        t_ncz = findViewById(R.id.ncMagZ);
        //Gyro Calibrated
        t_gX = findViewById(R.id.gX);
        t_gY = findViewById(R.id.gY);
        t_gZ = findViewById(R.id.gZ);
        //Gyro uncalibrated
        t_ugX = findViewById(R.id.ugX);
        t_ugY = findViewById(R.id.ugY);
        t_ugZ = findViewById(R.id.ugZ);


    }


    @Override
    protected void onResume() {
        super.onResume();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor ncMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        Sensor gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor ucGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);


        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, ncMagneticField, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, ucGyro, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);


    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //multiple sensors being listened to, find out which one it is and do something.
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magLog = event.values;
            magX = event.values[0];
            t_x.setText(getResources().getString(R.string.magX, magX));
            magY = event.values[1];
            t_y.setText(getResources().getString(R.string.magY, magY));
            magZ = event.values[2];
            t_z.setText(getResources().getString(R.string.magZ, magZ));

        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) {
            ucMagLog = event.values;
            ncMagX = event.values[0];
            t_ncx.setText(getResources().getString(R.string.ncMagX, ncMagX));
            ncMagY = event.values[1];
            t_ncy.setText(getResources().getString(R.string.ncMagY, ncMagY));
            ncMagZ = event.values[2];
            t_ncz.setText(getResources().getString(R.string.ncMagZ, ncMagZ));


        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroLog = event.values;
            gX = event.values[0];
            t_gX.setText((getResources().getString(R.string.gX, gX)));
            gY = event.values[1];
            t_gY.setText((getResources().getString(R.string.gY, gY)));
            gZ = event.values[2];
            t_gZ.setText((getResources().getString(R.string.gZ, gZ)));


        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED) {
            ucGyroLog = event.values;
            ugX = event.values[0];
            t_ugX.setText((getResources().getString(R.string.ugX, ugX)));
            ugY = event.values[1];
            t_ugY.setText((getResources().getString(R.string.ugY, ugY)));
            ugZ = event.values[2];
            t_ugZ.setText((getResources().getString(R.string.ugZ, ugZ)));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void saveLogToFile(View view) {
        String FILENAME = "sensor_data.csv";
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            String entry = createLogEntry();
            try {
                FileOutputStream out = openFileOutput(FILENAME, Context.MODE_APPEND);
                out.write(entry.getBytes());
                out.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

//    public void saveLogToFile(View view) {
//        String FILENAME = "magno_data.csv";
//        String entry = createLogEntry();
//
//        //append entry to csv file
//        try {
//            FileOutputStream out = openFileOutput(FILENAME, Context.MODE_APPEND);
//            out.write( entry.getBytes() );
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public String createLogEntry() {
        Instant t = now();
        return
                "magnetometer, " + t  + ", " + magLog[0] + ", " + magLog[1] + ", " + magLog[2] + "\n"
                        + "UCmagnetometer, " + t + ", " + ucMagLog[0] + ", " + ucMagLog[1] + ", " + ucMagLog[2] + "\n"
                        + "Gyroscope, " + t + ", " + gyroLog[0] + ", " + gyroLog[1] + ", " + gyroLog[2] + "\n"
                        + "UCGyroscope, " + t + ", " + ucGyroLog[0] + ", " + ucGyroLog[1] + ", " + ucGyroLog[2] + "\n";



    }

}


