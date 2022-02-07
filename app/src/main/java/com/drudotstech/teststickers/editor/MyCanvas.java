package com.drudotstech.teststickers.editor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.drudotstech.teststickers.BitmapModel;
import com.drudotstech.teststickers.CanvasUtils;

import java.util.ArrayList;
import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/2/2022 at 12:37 PM
 ******************************************************/


public class MyCanvas extends View {

    private static final String TAG = "Haxx";
    private static final int INVALID_VALUE = -1;
    private static final int SCALING_SENSITIVITY = 60; // lesser value = more sensitive
    private static final float MAX_SCALE_CHANGE = 0.3f; // the max change that should be made to scale at a time
    private static final float MIN_SCALE = 0.5f; // lesser value = more sensitive
    private static final float MAX_SCALE = 5; // lesser value = more sensitive
    private final int CLICK_DELAY = 300;// the delay (ms) between action down and up that will count as click

    private final Context context;
    private Bitmap sticker;
    private int screenWidth;
    private int screenHeight;
    private float centerX, centerY;

    private Rect screenRect;
    private Bitmap backgroundBitmap;
    private Paint paint;

    private List<BitmapModel> bitmaps = new ArrayList<>();
    private List<MyBitmap> views = new ArrayList<>();

    // for moving
    private int actionDownViewIndex;
    private int previousViewIndex = INVALID_VALUE;
    private long actionDownTime;
    private int clickedIndex = -1;
    private int dx, dy;
    private float dLeft, dTop, dRight, dBottom;

    // for rotation & scaling
    private PointF secondPoint = new PointF();
    private PointF firstPoint = new PointF();
    private int pointerId1 = INVALID_VALUE, pointerId2 = INVALID_VALUE;
    private float angle;
    private MyBitmap selectedView;
    private float startingScale = INVALID_VALUE;
    private float startingRotation = INVALID_VALUE;

    public MyCanvas(Context context) {
        super(context);
        this.context = context;
        screenWidth = CanvasUtils.getScreenWidth(context);
        screenHeight = CanvasUtils.getScreenHeight(context);
        centerX = (float) (screenWidth / 2.0);
        centerY = (float) (screenHeight / 2.0);

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(CanvasUtils.toPx(context, 4f));

        screenRect = new Rect(0, 0, screenWidth, screenHeight);

    }

    public void setSticker(Bitmap sticker) {
        this.sticker = sticker;
        invalidate();
    }

    public void addBitmap(BitmapModel myBitmap) {
        bitmaps.add(myBitmap);
        invalidate();
    }

    public void addBackgroundBitmap(Bitmap bitmap) {
        backgroundBitmap = bitmap;
        invalidate();
    }

    public void addBitmapView(MyBitmap myBitmap) {
        views.add(myBitmap);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if (bitmaps != null && !bitmaps.isEmpty()) {
//            for (BitmapModel B : bitmaps) {
//                canvas.drawBitmap(B.getBitmap(), B.getLeft(), B.getTop(), null);
//            }
//        }

        canvas.drawBitmap(backgroundBitmap, null, screenRect, null);

        if (views != null && !views.isEmpty()) {
            for (MyBitmap view : views) {
//                B.draw(canvas);

                //Lay the view out with the known dimensions
                view.layout(view.left, view.top, view.right, view.bottom);

                //Translate the canvas so the view is drawn at the proper coordinates
                canvas.save();
                canvas.translate(0, 0);

                //Draw the View and clear the translation
                view.draw(canvas);
                canvas.restore();
            }
        }
//        canvas.drawCircle(centerX, centerY, CanvasUtils.toPx(context, 50f), paint);
    }

    public Bitmap getFinalBitmap() {
        //this.measure(100, 100);
        //this.layout(0, 0, 100, 100);
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        return bmp;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean shouldInvalidate = false;

        final float rawX = event.getRawX();
        final float rawY = event.getRawY();
        final float X = event.getX();
        final float Y = event.getY();
        Log.d("Haxx", "--- " + rawX + " , " + rawY + "-------------- ");

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                // first touch pointer
                pointerId1 = event.getPointerId(event.getActionIndex());

                // note the action down time and the view index
                actionDownViewIndex = findSelectedViewIndex(rawX, rawY);

                // nothing is selected already
                if (selectedView == null) {

                    // touch a view/sticker
                    if (actionDownViewIndex != INVALID_VALUE) {

                        // move the selected view on top of the layers
                        moveViewToTop(actionDownViewIndex);
                        previousViewIndex = actionDownViewIndex;

                        // get selected view from the top
                        selectedView = views.get(views.size() - 1);

                        // make the touched view -> selected
                        selectedView.setClicked(true);

                        // save the starting rect
                        selectedView.updateStartingRect();


                        // calculate the difference from touch place to selected Rect
                        dLeft = (rawX - selectedView.rect.left);
                        dRight = (rawX - selectedView.rect.right);
                        dTop = (rawY - selectedView.rect.top);
                        dBottom = (rawY - selectedView.rect.bottom);
                        invalidate();
                    }
                }

                // Some View is selected already
                else {
                    // first make the previous selected View -> unselected
                    if (previousViewIndex != actionDownViewIndex)
                        views.get(previousViewIndex).setClicked(false);

                    // if clicked on a view/sticker
                    if (actionDownViewIndex != INVALID_VALUE) { // not clicked on empty screen


                        // move the selected view on top of the list
                        moveViewToTop(actionDownViewIndex);

                        // get selected view from the top
                        selectedView = views.get(views.size() - 1);

                        // make the touched view -> selected
                        selectedView.setClicked(true);

                        // save the starting rect
                        selectedView.updateStartingRect();

                        // calculate the difference from touch place to selected Rect
                        dLeft = (rawX - selectedView.rect.left);
                        dRight = (rawX - selectedView.rect.right);
                        dTop = (rawY - selectedView.rect.top);
                        dBottom = (rawY - selectedView.rect.bottom);
                    }
                    invalidate();
                }

                // note the action down time for detecting simple Click
                actionDownTime = System.currentTimeMillis();
                Log.d(TAG, "----------0 v 0--------------- " + actionDownViewIndex);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                Log.v(TAG, "ACTION_POINTER_DOWN");

                pointerId2 = event.getPointerId(event.getActionIndex());
                if (selectedView != null) {
                    getRawPoint(event, pointerId1, firstPoint);
                    getRawPoint(event, pointerId2, secondPoint);

                    // save the starting rect
                    selectedView.updateStartingRect();
                }

                break;

            case MotionEvent.ACTION_UP:

                // reset the first touch pointer
                pointerId1 = INVALID_VALUE;
                startingRotation = INVALID_VALUE;
                startingScale = INVALID_VALUE;

                if (selectedView != null) {
                    selectedView.isScaled = false;
                    selectedView.isTranslated = false;
                }

                // update the Rect from starting rect
//                selectedView.updateStartingRect();

                int index = findSelectedViewIndex(rawX, rawY);
                Log.d("Haxx", "----------o ^ o--------------- " + index);
                if (index == -1) break;

                break;

            case MotionEvent.ACTION_POINTER_UP:
                // reset the 2nd touch pointer
                pointerId2 = INVALID_VALUE;
                startingRotation = INVALID_VALUE;
                startingScale = INVALID_VALUE;
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d("Haxx", "---------- - - - ---------------- " + clickedIndex);

                // only perform actions if view is clicked/selected
                if (selectedView != null && selectedView.isClicked()) {

                    // ---------------------------------- Translating ------------------------------

                    // updating the view position when Action Up | Translating
                    RectF newRect = new RectF();

                    // to draw the bitmap from center of the touch point
//                    newRect.left = (int) (rawX - selectedView.rect.width() / 2.0);
//                    newRect.top = (int) (rawY - selectedView.rect.height() / 2.0);
//                    newRect.right = (int) newRect.left + selectedView.rect.width();
//                    newRect.bottom = (int) newRect.top + selectedView.rect.height();

                    // to draw the bitmap from left top of the touch point
//                    newRect.left = (int) (rawX);
//                    newRect.top = (int) (rawY);
//                    newRect.right = (int) newRect.left + selectedView.width;
//                    newRect.bottom = (int) newRect.top + selectedView.height;

                    // to draw the bitmap on the same place as before and move accordingly
                    newRect.left = (rawX - dLeft);
                    newRect.right = (rawX - dRight);
                    newRect.top = (rawY - dTop);
                    newRect.bottom = (rawY - dBottom);

                    selectedView.rect = newRect;
                    selectedView.isTranslated = true;

                    Log.d(TAG, "---------- new rect---------");
                    Log.d(TAG, "Left : " + selectedView.rect.left
                            + "  |  Top : " + selectedView.rect.top
                            + "  |  Right : " + selectedView.rect.right
                            + "  |  Bottom : " + selectedView.rect.bottom);


                    // --------------------------- Rotation & Scaling ------------------------------
                    if (pointerId1 != INVALID_VALUE && pointerId2 != INVALID_VALUE) {
                        PointF newSecondPoint = new PointF();
                        PointF newFirstPoint = new PointF();

                        // get touch points to calculate angle and distance
                        getRawPoint(event, pointerId1, newFirstPoint);
                        getRawPoint(event, pointerId2, newSecondPoint);

                        angle = angleBetweenLines(secondPoint, firstPoint, newSecondPoint, newFirstPoint);
                        Log.d(TAG, "---------- -Angle : " + angle + " --------- " + clickedIndex);

                        // set the starting rotation
                        if (startingRotation == INVALID_VALUE) {
                            startingRotation = selectedView.getRotation();
                        } else {
                            // add new rotation in the starting rotation
                            selectedView.setRotation(startingRotation + angle);
                            selectedView.isRotated = true;
                        }


                        // scaling the image
                        float distance = distanceBetweenLines(secondPoint, firstPoint, newSecondPoint, newFirstPoint);


                        // set the bitmap original scale as starting scale
                        if (startingScale == INVALID_VALUE) {
                            startingScale = selectedView.scale;
                        } else {
                            // calculate the scale from the distance
                            float scale = distance / SCALING_SENSITIVITY;
                            Log.d(TAG, " Scale :  " + scale + " ------- Distance : --> " + distance);
                            scale = startingScale + scale; // add new scale in starting scale
                            // set the new scale with within min & max limits
                            scale = getBoundedScale(scale);
                            selectedView.scale = scale;
                            selectedView.isScaled = true;

                            // when scaling the Rect, Also update the difference from touch place to selected Rect
//                            float halfScale = scale / 2.0f;
//                            dLeft = (rawX - selectedView.rect.left) * halfScale;
//                            dRight = dRight * halfScale;
//                            dTop = dTop * halfScale;
//                            dBottom = dBottom * halfScale;
//                            dRight = (rawX - selectedView.rect.right);
//                            dTop = (rawY - selectedView.rect.top);
//                            dBottom = (rawY - selectedView.rect.bottom);

//                            int scaledX = (int) ((selectedView.startingRectF.width() * scale) - selectedView.startingRectF.width());
//                            int scaledY = (int) ((selectedView.startingRectF.height() * scale) - selectedView.startingRectF.height());
//
//                            selectedView.rect.left = selectedView.startingRectF.left - scaledX;
//                            selectedView.rect.top = selectedView.startingRectF.top - scaledY;
//                            selectedView.rect.right = selectedView.startingRectF.right + scaledX;
//                            selectedView.rect.bottom = selectedView.startingRectF.bottom + scaledY;
                        }
                    }

                    // also update the selection border of the View
//                    selectedView.updateBorderRect(context);

                    // show the change
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                // reset values
                pointerId1 = INVALID_VALUE;
                pointerId2 = INVALID_VALUE;
                startingRotation = INVALID_VALUE;
                startingScale = INVALID_VALUE;
                if (selectedView != null) {
                    selectedView.isScaled = false;
                    selectedView.isTranslated = false;
                }
                break;

            default:
                return false;
        }

        return true;
    }

    private float getBoundedScale(float scale) {
        return Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
    }

    private void moveViewToTop(int index) {
        if (views != null && views.size() > index) {
            final MyBitmap removeView = views.remove(index); // removed from index
            views.add(removeView); // and added on top of the list
        }
    }

    // region H E L P E R S   M E T H O D S   F O R    G E S T U R E S

    private int findSelectedViewIndex(float x, float y) {
        int selectedIndex = INVALID_VALUE;
        for (int i = views.size() - 1; i >= 0; i--) {
            if (isInsideRect(x, y, views.get(i).borderRect)) {
                selectedIndex = i;
                break;
            }
        }
        return selectedIndex;
    }

    private boolean isInsideRect(float x, float y, RectF rect) {
        return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom;
    }

    void getRawPoint(MotionEvent ev, int index, PointF point) {
        final int[] location = {0, 0};
        selectedView.getLocationOnScreen(location);

        float x = ev.getX(index);
        float y = ev.getY(index);

        double angle = Math.toDegrees(Math.atan2(y, x));

        final float length = PointF.length(x, y);

        x = (float) (length * Math.cos(Math.toRadians(angle))) + location[0];
        y = (float) (length * Math.sin(Math.toRadians(angle))) + location[1];

        point.set(x, y);
    }

    private float angleBetweenLines(PointF secondPoint, PointF firstPoint, PointF newSecondPoint, PointF nweFirstPoint) {
        float angle1 = (float) Math.atan2((secondPoint.y - firstPoint.y), (secondPoint.x - firstPoint.x));
        float angle2 = (float) Math.atan2((newSecondPoint.y - nweFirstPoint.y), (newSecondPoint.x - nweFirstPoint.x));

        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return -angle;
    }

    private float distanceBetweenLines(PointF secondPoint, PointF firstPoint, PointF newSecondPoint, PointF newFirstPoint) {
        float beforeDistance =
                (float) Math.sqrt( // sqrt of sum of x, y points differences
                        Math.pow( // power of difference of x points
                                Math.max(firstPoint.x, secondPoint.x) // x1
                                        -                                     // difference
                                        Math.min(firstPoint.x, secondPoint.x) // x2
                                , 2) // power of 2
                                +
                                Math.pow( // power of differences of y points
                                        Math.max(firstPoint.y, secondPoint.y) // y 1
                                                -                             // difference
                                                Math.min(firstPoint.y, secondPoint.y) // y2
                                        , 2) // power of 2
                );

        float afterDistance =
                (float) Math.sqrt( // sqrt of sum of x, y points differences
                        Math.pow( // power of difference of x points
                                Math.max(newFirstPoint.x, newSecondPoint.x) // x1
                                        -                                     // difference
                                        Math.min(newFirstPoint.x, newSecondPoint.x) // x2
                                , 2) // power of 2
                                +
                                Math.pow( // power of differences of y points
                                        Math.max(newFirstPoint.y, newSecondPoint.y) // y 1
                                                -                             // difference
                                                Math.min(newFirstPoint.y, newSecondPoint.y) // y2
                                        , 2) // power of 2
                );

        final float diff = afterDistance - beforeDistance; // distance covered
        Log.d(TAG, " Distance :  " + beforeDistance + "  -  " + afterDistance + "  =  " + diff);
        return diff;
    }

    // endregion
}
