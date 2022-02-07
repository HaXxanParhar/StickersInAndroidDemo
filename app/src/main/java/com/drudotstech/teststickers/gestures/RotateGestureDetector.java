package com.drudotstech.teststickers.gestures;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/4/2022 at 11:46 AM
 ******************************************************/


import android.content.Context;
import android.view.MotionEvent;

public class RotateGestureDetector extends TwoFingerGestureDetector {
    private static final float PRESSURE = 0.6f;
    private static final float ROTATION = 0.2f;
    private float mCurrPressure;
    private float mPrevPressure;
    private OnRotateGestureListener listener;
    private boolean sloppyGesture;
    private boolean recognized;
    private float totalRotation;
    private float focusX;
    private float focusY;

    public RotateGestureDetector(Context context, OnRotateGestureListener listener) {
        super(context);
        this.listener = listener;
    }

    private void determineFocusPoint(MotionEvent curr) {
        focusX = (curr.getX(0) + curr.getX(1)) * 0.5f;
        focusY = (curr.getY(0) + curr.getY(1)) * 0.5f;
    }

    @Override
    protected void gestureStarted(int actionCode, MotionEvent event) {
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN) {
            resetState();
            previousEvent = MotionEvent.obtain(event);
            delta = 0;

            updateStateByEvent(event);
        } else if (actionCode == MotionEvent.ACTION_MOVE)
            if (!sloppyGesture)
                return;

        sloppyGesture = isSloppyGesture(event);
        if (!sloppyGesture)
            inProgress = true;
    }

    @Override
    protected void gestureProgress(int actionCode, MotionEvent event) {
        switch (actionCode) {
            case MotionEvent.ACTION_POINTER_UP:
                updateStateByEvent(event);
                if (!sloppyGesture && recognized)
                    resetState();
                break;

            case MotionEvent.ACTION_CANCEL:
                if (!sloppyGesture && recognized)
                    listener.onRotateEnd(this);
                resetState();
                break;

            case MotionEvent.ACTION_MOVE:
                updateStateByEvent(event);
                mCurrPressure = event.getPressure();
                if (mCurrPressure / mPrevPressure > PRESSURE) {
                    determineFocusPoint(event);
                    boolean updatePrevious;
                    if (recognized)
                        updatePrevious = listener.onRotate(this);
                    else {
                        updatePrevious = true;
                        recognized = Math.abs(totalRotation) >= ROTATION && listener.onRotateBegin(this);
                    }
                    if (updatePrevious) {
                        previousEvent.recycle();
                        previousEvent = MotionEvent.obtain(event);
                    }
                }
                mPrevPressure = event.getPressure();
                break;
        }
    }

    @Override
    protected void resetState() {
        super.resetState();
        sloppyGesture = false;
        recognized = false;
        totalRotation = 0;
    }

    @Override
    protected void updateStateByEvent(MotionEvent event) {
        super.updateStateByEvent(event);
        totalRotation += getRotationRadiansDelta();
    }

    public float getFocusX() {
        return focusX;
    }

    public float getFocusY() {
        return focusY;
    }

    public float getRotationRadiansDelta() {
        double diffRadians = Math.atan2(previousFingerDiffY, previousFingerDiffX) - Math.atan2(currentFingerDiffY, currentFingerDiffX);
        return (float) (diffRadians);
    }

    public interface OnRotateGestureListener {
        boolean onRotate(RotateGestureDetector detector);

        boolean onRotateBegin(RotateGestureDetector detector);

        void onRotateEnd(RotateGestureDetector detector);
    }
}