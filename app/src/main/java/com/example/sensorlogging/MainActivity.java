package com.example.sensorlogging;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    //magno stuff
    private final float[] magnetometerReading = new float[3]; //z, x, y
    private TextView t_x, t_y, t_z;
    private float magX, magY, magZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t_x = (TextView) findViewById(R.id.magX);
        t_y = (TextView) findViewById(R.id.magY);
        t_z = (TextView) findViewById(R.id.magZ);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor uMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        if(magneticField != null) {
            sensorManager.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, uMagneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.length);
        magX = event.values[0];
        t_x.setText(getResources().getString(R.string.magX, magX));
        magY = event.values[1];
        t_y.setText(getResources().getString(R.string.magY, magY));
        magZ = event.values[2];
        t_z.setText(getResources().getString(R.string.magZ, magZ));

        // ADD UNCALIBRATED MAGS HERE TO DISPLAY
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
