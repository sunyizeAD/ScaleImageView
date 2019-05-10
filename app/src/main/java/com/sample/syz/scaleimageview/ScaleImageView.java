package com.sample.syz.scaleimageview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.OverScroller;

public class ScaleImageView extends View implements GestureDetector.OnGestureListener
        , GestureDetector.OnDoubleTapListener ,Runnable{
    private float IMAGE_WIDTH = Utils.dp2px(200);
    private float OVER_SCALE = 1.5f;
    private Bitmap bitmap;

    private float offsetmarginX;
    private float offsetmarginY;
    private float offsetX;
    private float offsetY;

    private float smallScale;
    private float bigScale;

    private boolean isBig;

    //变化系数
    private float coefficient;
    private ObjectAnimator animater;
    private final GestureDetector gestureDetector;
    OverScroller scroller;

    public float getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(float coefficient) {
        this.coefficient = coefficient;
        invalidate();
    }

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        bitmap = Utils.getBitmap(getResources(), (int) IMAGE_WIDTH, R.drawable.bitmap2);
        gestureDetector = new GestureDetector(getContext(), this);
        scroller = new OverScroller(getContext());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(offsetX * coefficient, offsetY * coefficient);
        float scale = smallScale + (bigScale - smallScale) * coefficient;
        canvas.scale(scale, scale, getWidth() / 2, getHeight() / 2);
        canvas.drawBitmap(bitmap, offsetmarginX, offsetmarginY, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        offsetmarginX = (getWidth() - bitmap.getWidth()) / 2f;
        offsetmarginY = (getHeight() - bitmap.getHeight()) / 2f;

        if (bitmap.getWidth() / bitmap.getHeight() > getWidth() / getHeight()) {
            //胖型图，width放大系数小于height放大系数
            smallScale = (float) getWidth() / bitmap.getWidth();
            bigScale = (float) getHeight() / bitmap.getHeight() * OVER_SCALE;
        } else {
            bigScale = (float) getWidth() / bitmap.getWidth() * OVER_SCALE;
            smallScale = (float) getHeight() / bitmap.getHeight();
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (isBig) {
            offsetX -= distanceX;
            getOffsetX();
            offsetY -= distanceY;
            getOffsetY();
            invalidate();
        }
        return false;
    }

    private void getOffsetX() {
        offsetX = Math.min(offsetX, (bitmap.getWidth() * bigScale - getWidth()) / 2f);
        offsetX = Math.max(offsetX, -(bitmap.getWidth() * bigScale - getWidth()) / 2f);
    }

    private void getOffsetY() {
        offsetY = Math.min(offsetY, (bitmap.getHeight() * bigScale - getHeight()) / 2f);
        offsetY = Math.max(offsetY, -(bitmap.getHeight() * bigScale - getHeight()) / 2f);
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (isBig) {
            scroller.fling((int)offsetX, (int)offsetY, (int)velocityX, (int)velocityY,
                    (int)-((bitmap.getWidth() * bigScale - getWidth()) / 2f),
                    (int)((bitmap.getWidth() * bigScale - getWidth()) / 2f),
                    (int) -((bitmap.getHeight() * bigScale - getHeight()) / 2f),
                    (int)((bitmap.getHeight() * bigScale - getHeight()) / 2f));
            ViewCompat.postOnAnimation(this,this);
        }

        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        isBig = !isBig;
        if (isBig) {
            offsetX = (e.getX()-getWidth()/2f)*(1-bigScale/smallScale);
            getOffsetX();
            offsetY = (e.getY()-getHeight()/2f)*(1-bigScale/smallScale);
            getOffsetY();
            getObjectAnimater().start();
        } else {
            getObjectAnimater().reverse();
        }
        return false;
    }

    private ObjectAnimator getObjectAnimater() {
        if (animater == null) {
            animater = ObjectAnimator.ofFloat(this, "coefficient", 0, 1);
        }
        return animater;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public void run() {
        if (scroller.computeScrollOffset())
        {
            offsetX = scroller.getCurrX();
            offsetY = scroller.getCurrY();
            invalidate();
            ViewCompat.postOnAnimation(this,this);
        }
    }
}
