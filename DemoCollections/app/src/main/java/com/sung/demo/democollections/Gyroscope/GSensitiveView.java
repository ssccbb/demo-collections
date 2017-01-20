package com.sung.demo.democollections.Gyroscope;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.sung.demo.democollections.R;

/**
 * Created by sung on 2016/12/19.
 */

public class GSensitiveView extends ImageView {
    private Bitmap image;
    private double rotation;
    private Paint paint;

    public GSensitiveView(Context context,int resID) {
        super(context);
        BitmapDrawable drawble = (BitmapDrawable) context.getResources().getDrawable(resID);
        image = drawble.getBitmap();

        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);

        double w = image.getWidth();
        double h = image.getHeight();

        Rect rect = new Rect();
        getDrawingRect(rect);

        int degrees = (int) (180 * rotation / Math.PI);
        canvas.rotate(degrees, rect.width() / 2, rect.height() / 2);
        canvas.drawBitmap(image, //
                (float) ((rect.width() - w) / 2),//
                (float) ((rect.height() - h) / 2),//
                paint);
    }

    public void setRotation(double rad) {
        rotation = rad;
        invalidate();
    }
}
