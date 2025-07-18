package com.example.questionnairebuilder.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class CircleWithBorderTransformation extends BitmapTransformation {

    private final float borderWidth;
    private final int borderColor;

    public CircleWithBorderTransformation(Context context, float borderWidthDp, int borderColor) {
        this.borderWidth = borderWidthDp * context.getResources().getDisplayMetrics().density;
        this.borderColor = borderColor;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform,
                               int outWidth, int outHeight) {

        int size = Math.min(toTransform.getWidth(), toTransform.getHeight());
        int x = (toTransform.getWidth() - size) / 2;
        int y = (toTransform.getHeight() - size) / 2;

        Bitmap squared = Bitmap.createBitmap(toTransform, x, y, size, size);
        Bitmap result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(result);

        float r = size / 2f;

        // Draw border
        Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(borderColor);
        canvas.drawCircle(r, r, r, borderPaint);

        // Clip to a smaller circle and draw the image
        Path path = new Path();
        path.addCircle(r, r, r - borderWidth, Path.Direction.CCW);
        canvas.save();
        canvas.clipPath(path);

        canvas.drawBitmap(squared, 0, 0, null);
        canvas.restore();

        return result;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(("circle_with_border_" + borderWidth + borderColor).getBytes());
    }
}
