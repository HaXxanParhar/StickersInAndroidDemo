package com.drudotstech.teststickers;

import android.content.Context;
import android.util.TypedValue;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/2/2022 at 12:10 PM
 ******************************************************/


public class MyUtils {

    public static float toPx(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
