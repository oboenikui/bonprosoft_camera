package com.oboenikui.bonprosoft;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CameraActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private View mPreviewContainer;
    private float[] fAccell = null;
    private float[] fMagnetic = null;
    private BonprosoftView mBonprosoftView;

    Camera mCamera;
    CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        setTitle(R.string.title);
        mPreviewContainer = findViewById(R.id.camera_preview);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this);

        ((FrameLayout)mPreviewContainer).addView(mPreview);

        mBonprosoftView = new BonprosoftView(this);
        ((FrameLayout)findViewById(R.id.camera_preview_container)).addView(mBonprosoftView);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_UI);
        mPreview.reconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        mSensorManager.unregisterListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
        mPreview.release();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                fAccell = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                fMagnetic = event.values.clone();
                break;
        }
        if (fAccell != null && fMagnetic != null && event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float[] inR = new float[9];
            SensorManager.getRotationMatrix(
                    inR,
                    null,
                    fAccell,
                    fMagnetic);
            float[] outR = new float[9];
            SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);
            float[] fAttitude = new float[3];
            SensorManager.getOrientation(outR, fAttitude);
            Log.d("outR", Arrays.toString(fAttitude));

            mBonprosoftView.setCurrentOrient(fAttitude[0], fAttitude[1], fAttitude[2]);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
