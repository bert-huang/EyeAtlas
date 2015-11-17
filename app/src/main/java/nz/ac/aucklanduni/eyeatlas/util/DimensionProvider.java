package nz.ac.aucklanduni.eyeatlas.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import nz.ac.aucklanduni.eyeatlas.R;

public class DimensionProvider {
    private static Integer thumbnailHeight = null;
    private static Integer thumbnailWidth = null;
    private static Integer detailWidth = null;

    public static int getThumbnailHeight(Context context) {
        if (thumbnailHeight == null) {
            int cardViewHeight = (int) (context.getResources().getDimension(R.dimen.cardview_height) / context.getResources().getDisplayMetrics().density);
            Resources r = context.getResources();
            thumbnailHeight =  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, cardViewHeight, r.getDisplayMetrics());
        }
        return thumbnailHeight;
    }

    public static int getThumbnailWidth(Context context) {
        if (thumbnailWidth == null) {
            int gridViewMargin = (int) (context.getResources().getDimension(R.dimen.gridview_margin) / context.getResources().getDisplayMetrics().density);
            Resources r = context.getResources();
            int pxMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gridViewMargin * 2, r.getDisplayMetrics());
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            thumbnailWidth = size.x - pxMargin;
        }
        return thumbnailWidth;
    }

    public static int getDetailWidth(Context context) {
        if (detailWidth == null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            detailWidth = size.x;
        }
        return detailWidth;
    }

    public static int getDetailHeight(Context context) {
        if (detailWidth == null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            detailWidth = size.x;
        }
        return detailWidth;
    }
}
