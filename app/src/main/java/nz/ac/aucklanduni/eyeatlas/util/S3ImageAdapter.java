package nz.ac.aucklanduni.eyeatlas.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.BufferedInputStream;

import nz.ac.aucklanduni.eyeatlas.model.S3Properties;

public class S3ImageAdapter {

    public static String getThumbnailUrl(int id) {
        return id + "/thumbnail/thumbnail.jpg";
    }

    public static String getPreviewImageUrl(int id) {
        return id + "/preview/preview.jpg";
    }

    public static Bitmap getThumbnail(int id, S3Properties s3Properties) {

        String key = getThumbnailUrl(id);
        AmazonS3 s3Client = new AmazonS3Client(s3Properties);
        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));

        GetObjectRequest request = new GetObjectRequest(s3Properties.getBucketName(), key);
        S3Object object = s3Client.getObject(request);
        S3ObjectInputStream in = object.getObjectContent();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

        return bmp;
    }


    public static Bitmap getPreviewImage(int id, S3Properties s3Properties) {

        String key = getPreviewImageUrl(id);
        AmazonS3 s3Client = new AmazonS3Client(s3Properties);
        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));

        GetObjectRequest request = new GetObjectRequest(s3Properties.getBucketName(), key);
        S3Object object = s3Client.getObject(request);
        S3ObjectInputStream in = object.getObjectContent();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

        return bmp;
    }

    public static Bitmap getTile(String fileName, S3Properties s3Properties) {
        AmazonS3 s3Client = new AmazonS3Client(s3Properties);

        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));

        GetObjectRequest request = new GetObjectRequest(s3Properties.getBucketName(), fileName);
        S3Object object = s3Client.getObject(request);
        S3ObjectInputStream in = object.getObjectContent();
        BufferedInputStream bis = new BufferedInputStream(in);
        Bitmap bmp = DecryptionHandler.decryptImage(bis);
        return bmp;
    }
}
