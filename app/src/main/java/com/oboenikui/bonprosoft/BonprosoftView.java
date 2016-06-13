package com.oboenikui.bonprosoft;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;

/**
 * Created by Takaki Hoshikawa on 2016/06/13.
 */
public class BonprosoftView extends View {
    private Bitmap mBonprosoftBitmap;
    private double xdeg, ydeg, zrad, dx, dy;
    private boolean first = true;
    private Paint mPaint;
    private Matrix mMatrix;

    public BonprosoftView(Context context) {
        super(context);
        loadBitmap(context);
    }


    public BonprosoftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadBitmap(context);
    }

    public BonprosoftView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadBitmap(context);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    private void loadBitmap(Context context) {
        Bitmap bm = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.bonprosoft)).getBitmap();
        int width = getDisplayWidth();
        mBonprosoftBitmap = getResizedBitmap(bm, width, bm.getHeight() * width / bm.getWidth());
        mPaint = new Paint();
        mMatrix = new Matrix();
    }

    public void setCurrentOrient(double y, double x, double z) {
        this.xdeg = ((Math.toDegrees(x) + 90) % 360 - 90);
        this.ydeg = ((Math.toDegrees(y) + 90) % 360 - 90);
        this.zrad = z;
        invalidate();
        Log.d("ydeg", ydeg + "");
    }

    private boolean isBonprosoftVisible() {
        return xdeg <= 90 && xdeg >= -90 && ydeg <= 90 && ydeg >= -90;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isBonprosoftVisible()) {

            int width = getDisplayWidth();
            int height = getDisplayHeight();
            if (first) {
                first = false;
                dx = -ydeg * width / 90;
                dy = -xdeg * width / 90;
            } else {
                double tmpx = Math.abs(dx + ydeg * width / 90);
                double tmpy = Math.abs(dy + xdeg * width / 90);
                dx = tmpx <= 4 * width / 90 ? dx : ((-ydeg * height / 90 + dx * 2) / 3);
                dy = tmpy <= 4 * width / 90 ? dy : ((-xdeg * height / 90 + dy * 2) / 3);
                if (tmpx > 4 * width / 90 || tmpy > 4 * width / 90)
                    invalidate();
            }
            mMatrix.setRotate(-(float) Math.toDegrees(zrad), mBonprosoftBitmap.getWidth() / 2, mBonprosoftBitmap.getHeight() / 2);

            canvas.translate((float) (dx * Math.cos(zrad) + dy * Math.sin(zrad)) + width / 2 - mBonprosoftBitmap.getWidth() / 2, (float) (dy * Math.cos(zrad) - dx * Math.sin(zrad)) + height / 2 - mBonprosoftBitmap.getHeight() / 2);

            canvas.drawBitmap(mBonprosoftBitmap, mMatrix, mPaint);
        }
    }

    private int getDisplayWidth() {
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    private int getDisplayHeight() {
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
}
