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

    public static Bitmap getThumbnail(String name, Properties properties) {
        AmazonS3 s3Client = new AmazonS3Client(properties);
        Log.i("hi", properties.getAWSAccessKeyId());
        Log.i("hi", properties.getAWSSecretKey());
        Log.i("hi", properties.getBucketName());
        Log.i("hi", name);


        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));

        GetObjectRequest request = new GetObjectRequest(properties.getBucketName(), name + "/" + name + ".png");
        S3Object object = s3Client.getObject(request);
        S3ObjectInputStream in = object.getObjectContent();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
        return bmp;
    }
}
