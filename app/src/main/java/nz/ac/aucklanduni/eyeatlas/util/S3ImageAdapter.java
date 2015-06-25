package nz.ac.aucklanduni.eyeatlas.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.BufferedInputStream;

import nz.ac.aucklanduni.eyeatlas.model.Properties;

public class S3ImageAdapter {

    public static Bitmap getThumbnail(int id, Properties properties) {
        AmazonS3 s3Client = new AmazonS3Client(properties);

        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));

        GetObjectRequest request = new GetObjectRequest(properties.getBucketName(), id + "/thumbnail/thumbnail.jpg");
        S3Object object = s3Client.getObject(request);
        S3ObjectInputStream in = object.getObjectContent();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
        return bmp;
    }

    public static Bitmap getDetailImage(int id, Properties properties) {
        AmazonS3 s3Client = new AmazonS3Client(properties);

        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));

        GetObjectRequest request = new GetObjectRequest(properties.getBucketName(), id + "/preview/preview.jpg");
//        GetObjectRequest request = new GetObjectRequest(properties.getBucketName(), "9/500/img_0_0.jpg");
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
