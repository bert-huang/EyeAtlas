package nz.ac.aucklanduni.eyeatlas.model;

import android.content.Context;

import com.amazonaws.auth.AWSCredentials;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

import nz.ac.aucklanduni.eyeatlas.util.DecryptionHandler;
import nz.ac.aucklanduni.eyeatlas.util.FileReader;

public class S3Properties implements AWSCredentials {
    private static S3Properties instance;
    private static final String FILE_PATH = "s3.properties";

    private String accessKey;
    private String secretKey;
    private String bucketName;

    public static S3Properties getInstance(Context context) {
        if (instance == null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                HerokuProperties.getInstance();
                String jsonString = DecryptionHandler.decryptString(FileReader.fileStreamProvider(FILE_PATH, context));
                instance = mapper.readValue(jsonString, S3Properties.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String s3SecretKey) {
        this.secretKey = s3SecretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    public String getAWSAccessKeyId() {
        return this.accessKey;
    }

    @Override
    public String getAWSSecretKey() {
        return this.secretKey;
    }
}
