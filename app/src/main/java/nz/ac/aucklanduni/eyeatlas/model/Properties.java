package nz.ac.aucklanduni.eyeatlas.model;

import android.content.Context;

import com.amazonaws.auth.AWSCredentials;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

import nz.ac.aucklanduni.eyeatlas.util.FileReader;

public class Properties implements AWSCredentials {
    private static Properties instance;
    private static final String FILE_PATH = "properties.properties";
    private String herokuUrl;
    private String s3AccessKey;
    private String s3SecretKey;
    private String bucketName;

    public static Properties getInstance(Context context) {
        if (instance == null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                instance = mapper.readValue(FileReader.fileStreamProvider(FILE_PATH, context), Properties.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public String getHerokuUrl() {
        return herokuUrl;
    }

    public void setHerokuUrl(String herokuUrl) {
        this.herokuUrl = herokuUrl;
    }

    public String getS3AccessKey() {
        return s3AccessKey;
    }

    public void setS3AccessKey(String s3AccessKey) {
        this.s3AccessKey = s3AccessKey;
    }

    public String getS3SecretKey() {
        return s3SecretKey;
    }

    public void setS3SecretKey(String s3SecretKey) {
        this.s3SecretKey = s3SecretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    public String getAWSAccessKeyId() {
        return this.s3AccessKey;
    }

    @Override
    public String getAWSSecretKey() {
        return this.s3SecretKey;
    }
}
