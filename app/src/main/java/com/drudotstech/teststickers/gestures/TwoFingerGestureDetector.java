package com.drudotstech.teststickers.gestures;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/4/2022 at 11:44 AM
 ******************************************************/


import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public abstract class TwoFingerGestureDetector extends BaseGestureDetector {
    private final float edgeSlop;
    protected float previousFingerDiffX;
    protected float previousFingerDiffY;
    protected float currentFingerDiffX;
    protected float currentFingerDiffY;
    private float rightSlopEdge;
    private float bottomSlopEdge;
    private float currentLen;
    private float previoustLen;
    private Context mContext;
    private MotionEvent mPrevEvent;

    public TwoFingerGestureDetector(Context context) {
        mContext = context;
        ViewConfiguration config = ViewConfiguration.get(context);
        edgeSlop = config.getScaledEdgeSlop();
    }

    protected static float getRawX(MotionEvent event, int pointerIndex) {
        float offset = event.getX() - event.getRawX();
        if (pointerIndex < event.getPointerCount())
            return event.getX(pointerIndex) - offset;

        return 0f;
    }

    protected static float getRawY(MotionEvent event, int pointerIndex) {
        float offset = event.getY() - event.getRawY();
        if (pointerIndex < event.getPointerCount())
            return event.getY(pointerIndex) - offset;

        return 0f;
    }

    @Override
    protected abstract void gestureStarted(int actionCode, MotionEvent event);

    @Override
    protected abstract void gestureProgress(int actionCode, MotionEvent event);

    protected void updateStateByEvent(MotionEvent curr) {
        super.updateStateByEvent(curr);

        if (mPrevEvent == null) {
            mPrevEvent = curr;
            return;
        }

        final MotionEvent prev = mPrevEvent;

        currentLen = -1;
        previoustLen = -1;

        // Previous
        final float px0 = prev.getX(0);
        final float py0 = prev.getY(0);
        final float px1 = prev.getX(1);
        final float py1 = prev.getY(1);
        final float pvx = px1 - px0;
        final float pvy = py1 - py0;
        previousFingerDiffX = pvx;
        previousFingerDiffY = pvy;

        // Current
        final float cx0 = curr.getX(0);
        final float cy0 = curr.getY(0);
        final float cx1 = curr.getX(1);
        final float cy1 = curr.getY(1);
        final float cvx = cx1 - cx0;
        final float cvy = cy1 - cy0;
        currentFingerDiffX = cvx;
        currentFingerDiffY = cvy;
    }

    public float getCurrentSpan() {
        if (currentLen == -1) {
            final float cvx = currentFingerDiffX;
            final float cvy = currentFingerDiffY;
            currentLen = (float) Math.sqrt(cvx * cvx + cvy * cvy);
        }
        return currentLen;
    }

    public float getPreviousSpan() {
        if (previoustLen == -1) {
            final float pvx = previousFingerDiffX;
            final float pvy = previousFingerDiffY;
            previoustLen = (float) Math.sqrt(pvx * pvx + pvy * pvy);
        }
        return previoustLen;
    }

    protected boolean isSloppyGesture(MotionEvent event) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        rightSlopEdge = metrics.widthPixels - edgeSlop;
        bottomSlopEdge = metrics.heightPixels - edgeSlop;

        final float x0 = event.getRawX();
        final float y0 = event.getRawY();
        final float x1 = getRawX(event, 1);
        final float y1 = getRawY(event, 1);

        boolean sloppy0 = x0 < edgeSlop || y0 < edgeSlop || x0 > rightSlopEdge || y0 > bottomSlopEdge;
        boolean sloppy1 = x1 < edgeSlop || y1 < edgeSlop || x1 > rightSlopEdge || y1 > bottomSlopEdge;

        if (!sloppy0 && !sloppy1)
            return false;

        return true;
    }
}