package nz.ac.aucklanduni.eyeatlas.util;

import android.graphics.Bitmap;
import android.util.LruCache;

public class ImageLruCache {

    private LruCache<String, Bitmap> mMemoryCache;
    private static ImageLruCache cache;

    public static ImageLruCache getInstance() {
        if (cache == null) {
            cache = new ImageLruCache();
        }
        return cache;
    }

    private ImageLruCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

}
