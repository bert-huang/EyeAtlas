package nz.ac.aucklanduni.eyeatlas.util;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import nz.ac.aucklanduni.eyeatlas.model.Properties;

public class DecodeKeyHandler {

    private static int pswdIterations = 65536;
    private static int keySize = 128;
    private static String algorithm = "AES";
    private static String transformation = "AES/CBC/PKCS5Padding";

    private String password;
    private String KEY;

    private static DecodeKeyHandler decodeKeyHandler;

    public static DecodeKeyHandler getInstance(Context context) {
        if (decodeKeyHandler == null) {

            decodeKeyHandler = new DecodeKeyHandler(context);
        }

        return decodeKeyHandler;
    }

    private DecodeKeyHandler(Context context) {

        // Fetch from keystore
        KEY = SecurityModule.GetData();
        if(KEY == null) {
            SecureRandom random = new SecureRandom();
            password = new BigInteger(130, random).toString(32);

            String url = Properties.getInstance(context).getHerokuUrl() + "rest/key";
            KeyLoader loader = new KeyLoader();
            loader.execute(url);

        }
    }

    private String decode(String encryptedKey){
        byte[] salt;
        byte[] ivBytes;
        byte[] encryptedKeyBytes;
        byte[] decryptedKeyBytes;

        try {

            encryptedKeyBytes = Base64.decode(encryptedKey.split("_")[0], Base64.DEFAULT);
            salt = Base64.decode(encryptedKey.split("_")[1], Base64.DEFAULT);
            ivBytes = Base64.decode(encryptedKey.split("_")[2], Base64.DEFAULT);

            // Derive the new key
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    pswdIterations,
                    keySize
            );

            SecretKey secretKey = factory.generateSecret(spec);
            SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), algorithm);

            // Decrypt the key
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));

            decryptedKeyBytes = cipher.doFinal(encryptedKeyBytes);
            String decryptedKey = new String(decryptedKeyBytes);

            return decryptedKey;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    class KeyLoader extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            Log.i("XEYE", "Fetching key from remote...");
        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", password);


                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPostRequest = new HttpPost(urls[0]);
                StringEntity se = new StringEntity(jsonObject.toString());
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                httpPostRequest.setEntity(se);
                HttpResponse response = httpClient.execute(httpPostRequest);

                String responseText = EntityUtils.toString(response.getEntity());
                String result = decode(responseText);

                new StoreDataTask().execute(result);

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            KEY = result;

        }
    }

    class StoreDataTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.i("XEYE", "Storing key...");
        }

        @Override
        protected Void doInBackground(String... datas) {
            SecurityModule.StoreData(datas[0]);

            return null;
        }
    }
}
