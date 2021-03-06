package nz.ac.aucklanduni.eyeatlas.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
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

    public static String getThumbnailUrl(int id) {
        return id + "/thumbnail/thumbnail.jpg";
    }

    public static String getPreviewImageUrl(int id) {
        return id + "/preview/preview.jpg";
    }

    public static Bitmap getThumbnail(int id, Properties properties) {

        String key = getThumbnailUrl(id);
        AmazonS3 s3Client = new AmazonS3Client(properties);
        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));

        GetObjectRequest request = new GetObjectRequest(properties.getBucketName(), key);
        S3Object object = s3Client.getObject(request);
        S3ObjectInputStream in = object.getObjectContent();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

        return bmp;
    }


    public static Bitmap getPreviewImage(int id, Properties properties) {

        String key = getPreviewImageUrl(id);
        AmazonS3 s3Client = new AmazonS3Client(properties);
        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));

        GetObjectRequest request = new GetObjectRequest(properties.getBucketName(), key);
        S3Object object = s3Client.getObject(request);
        S3ObjectInputStream in = object.getObjectContent();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

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
