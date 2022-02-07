package com.drudotstech.teststickers.gestures;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/4/2022 at 11:41 AM
 ******************************************************/


import android.view.MotionEvent;

public abstract class BaseGestureDetector {
    protected boolean inProgress;

    protected MotionEvent previousEvent;
    protected MotionEvent currentEvent;

    protected float currentPress;
    protected float previousPress;
    protected long delta;
    protected long mTimeDelta;

    public BaseGestureDetector() {
    }

    protected abstract void gestureStarted(int actionCode, MotionEvent event);

    protected abstract void gestureProgress(int actionCode, MotionEvent event);

    public boolean onTouchEvent(MotionEvent event) {
        if (!inProgress)
            gestureStarted(event.getActionMasked(), event);
        else
            gestureProgress(event.getActionMasked(), event);

        return true;
    }

    protected void updateStateByEvent(MotionEvent current) {
        final MotionEvent previous = previousEvent;

        if (currentEvent != null) {
            currentEvent.recycle();
            currentEvent = null;
        }
        currentEvent = MotionEvent.obtain(current);

        mTimeDelta = current.getEventTime() - previous.getEventTime();

        currentPress = current.getPressure(current.getActionIndex());
        previousPress = previous.getPressure(previous.getActionIndex());
    }

    protected void resetState() {
        if (previousEvent != null) {
            previousEvent.recycle();
            previousEvent = null;
        }
        if (currentEvent != null) {
            currentEvent.recycle();
            currentEvent = null;
        }
        inProgress = false;
    }
}