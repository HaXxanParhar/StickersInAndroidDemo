package com.drudotstech.teststickers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class TestLayersActivity extends AppCompatActivity {

    private final Context context = TestLayersActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_layers);

        final LayeredImageView layeredImageView = new LayeredImageView(this);
        Resources res = layeredImageView.getResources();

        layeredImageView.setImageResource(R.drawable.original);

        Matrix matrix = new Matrix();
        matrix.preTranslate(81, 146); // pixels to offset
        final LayeredImageView.Layer layer1 = layeredImageView.addLayer(ContextCompat.getDrawable(context, R.drawable.love_1), matrix);

        matrix = new Matrix();
        matrix.preTranslate(62, 63); // pixels to offset
        final LayeredImageView.Layer layer0 = layeredImageView.addLayer(ContextCompat.getDrawable(context, R.drawable.love_2), matrix);


        final AnimationDrawable animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(false);
        Drawable frame1, frame2;
        frame1 = ContextCompat.getDrawable(context, R.drawable.love_1);
        frame2 = ContextCompat.getDrawable(context, R.drawable.love_2);
        animationDrawable.addFrame(frame1, 3000);
        animationDrawable.addFrame(frame2, 1000);
        animationDrawable.addFrame(frame1, 250);
        animationDrawable.addFrame(frame2, 250);
        animationDrawable.addFrame(frame1, 250);
        animationDrawable.addFrame(frame2, 250);
        animationDrawable.addFrame(frame1, 250);
        animationDrawable.addFrame(frame2, 250);
        animationDrawable.setBounds(200, 20, 300, 120);
        layeredImageView.addLayer(1, animationDrawable);
        layeredImageView.post(new Runnable() {
            @Override
            public void run() {
                animationDrawable.start();
            }
        });

        int[] colors = {
                0xeeffffff,
                0xee0038a8,
                0xeece1126,
        };
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        gradientDrawable.setBounds(0, 0, 100, 129);
        gradientDrawable.setCornerRadius(20);
        gradientDrawable.setStroke(5, 0xaa666666);
        final Matrix mm = new Matrix();
        mm.preTranslate(200, 69); // pixels to offset
        mm.preRotate(20, 50, 64.5f);
        final LayeredImageView.Layer layer2 = layeredImageView.addLayer(2, gradientDrawable, mm);

        final Animation as = AnimationUtils.loadAnimation(this, R.anim.anim_set);

        final Runnable action1 = new Runnable() {
            @Override
            public void run() {
                Animation a;
                android.view.animation.Interpolator i;

                i = input -> (float) Math.sin(input * Math.PI);


                as.setInterpolator(i);
                layer0.startLayerAnimation(as);

                a = new TranslateAnimation(0, 0, 0, 100);
                a.setDuration(3000);
                i = input -> {
                    float output = (float) Math.sin(Math.pow(input, 2.5f) * 12 * Math.PI);
                    return (1 - input) * output;
                };
                a.setInterpolator(i);
                layer1.startLayerAnimation(a);

                a = new AlphaAnimation(0, 1);
                i = input -> (float) (1 - Math.sin(input * Math.PI));
                a.setInterpolator(i);
                a.setDuration(2000);
                layer2.startLayerAnimation(a);
            }
        };

        layeredImageView.setOnClickListener(view -> action1.run());
        layeredImageView.postDelayed(action1, 2000);

//    final float[] values = new float[9];
//    final float[] pts = new float[2];
//    final Matrix inverse = new Matrix();;
//    OnTouchListener l = new OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent event) {
//            int action = event.getAction();
//            if (action != MotionEvent.ACTION_UP) {
//                if (inverse.isIdentity()) {
//                    layeredImageView.getImageMatrix().invert(inverse);
//                    Log.d(TAG, "onTouch set inverse");
//                }
//                pts[0] = event.getX();
//                pts[1] = event.getY();
//                inverse.mapPoints(pts);
//
//                mm.getValues(values);
//                // gradientDrawable's bounds are (0, 0, 100, 129);
//                values[Matrix.MTRANS_X] = pts[0] - 100 / 2;
//                values[Matrix.MTRANS_Y] = pts[1] - 129 / 2;
//                mm.setValues(values);
//                layeredImageView.invalidate();
//            }
//            return false;
//        }
//    };
//    layeredImageView.setOnTouchListener(l);
        setContentView(layeredImageView);
    }
}