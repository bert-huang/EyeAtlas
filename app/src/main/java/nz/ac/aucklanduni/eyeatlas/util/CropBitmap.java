package nz.ac.aucklanduni.eyeatlas.util;

import android.graphics.Bitmap;

public class CropBitmap {
    public static Bitmap cropBitmap(Bitmap src, int viewWidth, int viewHeight) {
        int width = viewWidth;
        int height = viewHeight;
        float viewRatio = (float) viewWidth / (float) viewHeight;
        float srcRatio = (float) src.getWidth() / (float) src.getHeight();

        if (width > src.getWidth()) {
            width = src.getWidth();
        }

        if (height > src.getHeight()) {
            height = src.getHeight();
        }

        if (srcRatio < viewRatio) {
            height = (int) (height * srcRatio / viewRatio);
        } else {
            width = (int) (width * viewRatio / srcRatio);
        }

        int x = (int) (src.getWidth() - width)/2;
        int y = (int) (src.getHeight() - height)/2;

        return Bitmap.createBitmap(src, x, y, width, height);
    }
}
