package com.drudotstech.teststickers;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.drudotstech.teststickers.gestures.RotationGestureDetector;

public class GesturesActivity extends AppCompatActivity {

    ImageView cow;
    RotationGestureDetector rotateGestureDetector;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestures);

        cow = findViewById(R.id.cow);

        cow.setOnTouchListener((view, motionEvent) -> rotateGestureDetector.onTouchEvent(motionEvent));

        rotateGestureDetector = new RotationGestureDetector(cow, rotationDetector -> {
            Log.d("haxx", " Rotation : " + rotationDetector.getAngle());
            cow.animate().setDuration(10).rotationBy(rotationDetector.getAngle());
        });
    }
}