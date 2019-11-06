package com.sadwave.events.util;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import org.jetbrains.annotations.NotNull;

/**
 * Закругление углов и возможность отступа от краев изображения
 * это дубль класса из модуля MsRestaurantsLibImpl
 *
 * @author Alexandr Rusin
 */
public class RoundedCornersTransformation{
    private final int mRadius;
    private final int mMargin;

    /**
     * @param radius радиус закругления
     * @param margin отступ от краев
     */
    public RoundedCornersTransformation(int radius, int margin) {
        mRadius = radius;
        mMargin = margin;
    }

    public Bitmap transform(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        drawRoundRect(canvas, paint, width, height);
        source.recycle();

        return bitmap;
    }

    @NotNull
    public String key() {
        return "RoundedTransformation(radius=" + mRadius + ", margin=" + mMargin + ")";
    }

    private void drawRoundRect(Canvas canvas, Paint paint, float width, float height) {
        float right = width - mMargin;
        float bottom = height - mMargin;

        canvas.drawRoundRect(new RectF(mMargin, mMargin, right, bottom), mRadius, mRadius, paint);
    }
}