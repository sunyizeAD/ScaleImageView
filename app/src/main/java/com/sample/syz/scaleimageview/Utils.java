package com.sample.syz.scaleimageview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;

public class Utils {
    public static float dp2px(int dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,Resources.getSystem().getDisplayMetrics());
    }
    public static Bitmap getBitmap(Resources res,int width,int id) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res,id,options);
        options.inJustDecodeBounds = false;
        options.inDensity = options.outWidth;
        options.inTargetDensity = width;
        return BitmapFactory.decodeResource(res,id,options);
    }

    public static float getCameraZ() {
        return -6* Resources.getSystem().getDisplayMetrics().density;
    }
}
