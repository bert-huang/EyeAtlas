package nz.ac.aucklanduni.eyeatlas.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.BufferedInputStream;

import nz.ac.aucklanduni.eyeatlas.activities.MainActivity;
import nz.ac.aucklanduni.eyeatlas.model.Properties;

public class S3ImageAdapter {

    private static Bitmap getImageFromCache(String key) {
        BitmapDrawable bmpDrbl;

        bmpDrbl = MainActivity.imageCache.getBitmapFromMemCache(key);
        if (bmpDrbl != null) {
            return bmpDrbl.getBitmap();
        }

        Bitmap bmp;

        bmp = MainActivity.imageCache.getBitmapFromDiskCache(key);
        if (bmp != null) {
            return bmp;
        }

        return null;
    }

    public static Bitmap getThumbnail(int id, Properties properties, Context context) {
        String key = id + "/thumbnail/thumbnail.jpg";
        Bitmap bmp = getImageFromCache(key);

        if (bmp != null) {
            return bmp;
        }

        AmazonS3 s3Client = new AmazonS3Client(properties);

        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));

        GetObjectRequest request = new GetObjectRequest(properties.getBucketName(), key);
        S3Object object = s3Client.getObject(request);
        S3ObjectInputStream in = object.getObjectContent();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        bmp = BitmapFactory.decodeStream(bufferedInputStream);

        MainActivity.imageCache.addBitmapToCache(key, new BitmapDrawable(context.getResources(), bmp));

        return bmp;
    }

    public static Bitmap getDetailImage(int id, Properties properties, Context context) {
        String key = id + "/preview/preview.jpg";
        Bitmap bmp = getImageFromCache(key);

        if (bmp != null) {
            return bmp;
        }

        AmazonS3 s3Client = new AmazonS3Client(properties);

        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));

        GetObjectRequest request = new GetObjectRequest(properties.getBucketName(), key);
        S3Object object = s3Client.getObject(request);
        S3ObjectInputStream in = object.getObjectContent();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        bmp = BitmapFactory.decodeStream(bufferedInputStream);

        MainActivity.imageCache.addBitmapToCache(key, new BitmapDrawable(context.getResources(), bmp));

        return bmp;
    }

    public static Bitmap getTile(String fileName, Properties properties) {
        AmazonS3 s3Client = new AmazonS3Client(properties);

        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));

        GetObjectRequest request = new GetObjectRequest(properties.getBucketName(), fileName);
        S3Object object = s3Client.getObject(request);
        S3ObjectInputStream in = object.getObjectContent();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
        return bmp;
    }
}
