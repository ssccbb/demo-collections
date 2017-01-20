package com.sung.demo.democollections.Gyroscope;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sung.demo.democollections.R;

import java.util.Timer;
import java.util.TimerTask;

public class Gyroscope extends AppCompatActivity implements SensorEventListener{
    private GSensitiveView gsView;
    private SensorManager sm;
    private TextView showTextView;
    private LinearLayout showImgCon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gyroscope_main);
        initView();
    }

    private void initView(){
        showTextView = (TextView) findViewById(R.id.gyroscope_text);
        showImgCon = (LinearLayout) findViewById(R.id.gyroscope_con);

        gsView = new GSensitiveView(this,R.mipmap.gyroscope_flag);
        showImgCon.addView(gsView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDestroy() {
        sm.unregisterListener(this);
        super.onDestroy();
    }

    public void onSensorChanged(SensorEvent event) {
        if (Sensor.TYPE_ACCELEROMETER != event.sensor.getType()) {
            return;
        }

        float[] values = event.values;
        float ax = values[0];
        float ay = values[1];

        double g = Math.sqrt(ax * ax + ay * ay);
        double cos = ay / g;
        if (cos > 1) {
            cos = 1;
        } else if (cos < -1) {
            cos = -1;
        }
        double rad = Math.acos(cos);
        if (ax < 0) {
            rad = 2 * Math.PI - rad;
        }

        int uiRot = getWindowManager().getDefaultDisplay().getRotation();
        double uiRad = Math.PI / 2 * uiRot;
        rad -= uiRad;

        showTextView.setText(rad+"");
        gsView.setRotation(rad);
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }


}
