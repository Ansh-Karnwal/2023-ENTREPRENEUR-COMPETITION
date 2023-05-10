package com.example.entcomp2023;

import static android.util.Half.EPSILON;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.example.entcomp2023.databinding.ActivityMainBinding;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //binding to access parts of program
    private ActivityMainBinding binding;
    //get instance of accelerometer
    private Sensor accelerometer;
    //instance of sensor manager
    private SensorManager sensorManager;
    //pitch, tilt, azimuth variables for new function
    private double pitch, tilt, azimuth;

    //assigning instance variables and registering and populating pieces (exoskeleton parts)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //function name is what function does, convert floats to doubles (decimal numbers)
    private double[] convertFloatsToDoubles(float[] input) {
        if (input == null)
            return null;
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++)
            output[i] = input[i];
        return output;
    }

    //
    @Override
    public void onSensorChanged(SensorEvent event) {
        double[] g = convertFloatsToDoubles(event.values.clone());

        //Normalize
        double norm = Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2] + g[3] * g[3]);
        g[0] /= norm;
        g[1] /= norm;
        g[2] /= norm;
        g[3] /= norm;
        double x = g[0];
        double y = g[1];
        double z = g[2];
        double w = g[3];
        double sinP = 2.0 * (w * x + y * z);
        double cosP = 1.0 - 2.0 * (x * x + y * y);
        //Calculate Pitch in degrees (-180 to 180)
        pitch = Math.atan2(sinP, cosP) * (180 / Math.PI);
        double sinT = 2.0 * (w * y - z * x);
        //Calculate Tilt in degrees (-90 to 90)
        if (Math.abs(sinT) >= 1)
            tilt = Math.copySign(Math.PI / 2, sinT) * (180 / Math.PI);
        else
            tilt = Math.asin(sinT) * (180 / Math.PI);
        double sinA = 2.0 * (w * z + x * y);
        double cosA = 1.0 - 2.0 * (y * y + z * z);
        //Calculate Azimuth in degrees (0 to 360; 0 = North, 90 = East, 180 = South, 270 = West)
        azimuth = Math.atan2(sinA, cosA) * (180 / Math.PI);
        binding.xAxisView.setText("Pitch: " + pitch);
        binding.yAxisView.setText("Tilt: " + tilt);
        binding.zAxisView.setText("Azimuth: " + azimuth);
        moveMotor(pitch, tilt, azimuth);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    private void moveMotor(double pitch, double tilt, double azimuth) {
        //valid direction
        if (azimuth >=-360 && azimuth <= 360) {
            //rotation gets multiplied by the pitch (in a real world case the force the human puts)
            if ((pitch >= 0 && pitch <= 10)) {
                binding.skeletonPiece.setRotation(((float) pitch) * 1.1F) ;
            }
            if ((pitch >= 11 && pitch <= 30)) {
                binding.skeletonPiece.setRotation(((float) pitch) * 1.6F) ;
            }
            if ((pitch >= 31 && pitch <= 50)) {
                binding.skeletonPiece.setRotation(((float) pitch) * 2.8F) ;
            }
            if ((pitch >= 51 && pitch <= 70)) {
                binding.skeletonPiece.setRotation(((float) pitch) * 4.4F) ;
            }
            if (pitch >= 71) {
                binding.skeletonPiece.setRotation(((float) pitch) * 7.6F) ;
            }
            if ((pitch >= -10 && pitch <= 0)) {
                binding.skeletonPiece.setRotation(((float) pitch) * 1.1F) ;
            }
            if ((pitch >= -30 && pitch <= -11)) {
                binding.skeletonPiece.setRotation(((float) pitch) * 1.6F) ;
            }
            if ((pitch >= -50 && pitch <= -31)) {
                binding.skeletonPiece.setRotation(((float) pitch) * 2.8F) ;
            }
            if ((pitch >= -70 && pitch <= -51)) {
                binding.skeletonPiece.setRotation(((float) pitch) * 4.4F) ;
            }
            if (pitch <= -71) {
                binding.skeletonPiece.setRotation(((float) pitch) * 7.6F) ;
            }
        }
    }
}