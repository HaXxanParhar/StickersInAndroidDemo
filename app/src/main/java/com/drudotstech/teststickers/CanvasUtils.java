package com.drudotstech.teststickers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/2/2022 at 12:43 PM
 ******************************************************/


public class CanvasUtils {


    public static Bitmap copyBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        return Bitmap.createBitmap(bm, 0, 0, width, height, null, false);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    public static Bitmap getResizedBitmap(Context context, Bitmap bm) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) getScreenWidth(context)) / width;
        float scaleHeight = ((float) getScreenHeight(context)) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static float toPx(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
