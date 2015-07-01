package nz.ac.aucklanduni.eyeatlas.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.LruCache;

import com.jakewharton.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageLruCache {



    // Memory Cache Variables
    private LruCache<String, Bitmap> mMemoryCache;

    // Disk Cache Variables
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "s3_images";
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final int COMPRESSION_QUALITY = 80;
    private static final BitmapFactory.Options BITMAPFACTORY_OPTIONS = new BitmapFactory.Options();

    // Singleton object
    private static ImageLruCache cache;

    public static ImageLruCache getInstance(Context context) {
        if (cache == null) {
            cache = new ImageLruCache(context);
        }
        return cache;
    }

    private ImageLruCache(Context context) {
        initMemoryCache();
        initDiskCache(context);
    }

    private void initMemoryCache(){
        // Init Memory Cache
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

    public void addBitmapToCache(String key, Bitmap bitmap) {

        key = key.replaceAll("/", "_").replaceAll(".jpg", "");

        addBitmapToMemoryCache(key, bitmap);
        addBitmapToDiskCache(key, bitmap);
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private void addBitmapToDiskCache(String key, Bitmap bitmap) {

        synchronized (mDiskCacheLock) {
            try {
                if (mDiskLruCache != null && mDiskLruCache.get(key) == null) {
                    DiskLruCache.Editor editor = null;
                    try {
                        editor = mDiskLruCache.edit( key );
                        if ( editor == null ) {
                            return;
                        }
                        OutputStream output = null;
                        try {
                            output = new BufferedOutputStream(editor.newOutputStream(0), IO_BUFFER_SIZE);
                            boolean compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, output);
                            if ( compressed ) {
                                mDiskLruCache.flush();
                                editor.commit();
                            } else {
                                editor.abort();
                            }
                        } finally {
                            if ( output != null ) {
                                output.close();
                            }
                        }
                    } catch ( Exception e ) {
                        try {
                            if ( editor != null ) {
                                editor.abort();
                            }
                        } catch ( IOException io ) {
                            io.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getBitmapFromCache(String key) {
        Bitmap bmp;
        key = key.replaceAll("/", "_").replaceAll(".jpg", "");

        bmp = getBitmapFromMemoryCache(key);
        if (bmp == null) {
            bmp = getBitmapFromDiskCache(key);
            if(bmp != null) {
                addBitmapToMemoryCache(key, bmp); // Re-add bitmap to memory cache
            }
        }
        return bmp;
    }

    private Bitmap getBitmapFromDiskCache(String key) {
        if ( mDiskLruCache == null ) {
            return null;
        }
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskLruCache.get( key );
            if ( snapshot == null ) {
                return null;
            }
            final InputStream input = snapshot.getInputStream( 0 );
            if ( input != null ) {
                BufferedInputStream buffered = new BufferedInputStream( input, IO_BUFFER_SIZE );
                bitmap = BitmapFactory.decodeStream(buffered, null, BITMAPFACTORY_OPTIONS);
            }
        } catch ( Exception e ) {

        } finally {
            if ( snapshot != null ) {
                snapshot.close();
            }
        }
        return bitmap;
    }

    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    private void initDiskCache(Context context) {
        File cacheDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR);
        new InitDiskCacheTask().execute(cacheDir);
    }

    class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                try {
                    mDiskLruCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE);
                    mDiskCacheStarting = false; // Finished initialization
                    mDiskCacheLock.notifyAll(); // Wake any waiting threads
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Environment.isExternalStorageRemovable() ?
                        context.getExternalCacheDir().getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

}
