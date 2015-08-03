package nz.ac.aucklanduni.eyeatlas.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import nz.ac.aucklanduni.eyeatlas.model.EyeAtlas;
import nz.ac.aucklanduni.eyeatlas.model.HerokuProperties;
import nz.ac.aucklanduni.eyeatlas.model.S3Properties;

public class DecryptionHandler {

    private static int pswdIterations = 65536;
    private static int keySize = 128;
    private static String algorithm = "AES";
    private static String transformation = "AES/CBC/PKCS5Padding";
    public static boolean READY = false;

    private String password;
    private String KEY;
    private String IV;

    private static DecryptionHandler decodeKeyHandler;

    public static DecryptionHandler getInstance() {
        if (decodeKeyHandler == null) {

            decodeKeyHandler = new DecryptionHandler();
        }

        return decodeKeyHandler;
    }

    public static String decryptString(InputStream inStream) {

        try {

            Cipher cipher = Cipher.getInstance(transformation);
            SecretKeySpec secretKey = new SecretKeySpec(DecryptionHandler.getInstance().KEY.getBytes(), algorithm);
            IvParameterSpec ivSpec = new IvParameterSpec(DecryptionHandler.getInstance().IV.getBytes());

            /**
             * Initialize the cipher for decrytion
             */
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            /**
             * Initialize input and output streams
             */
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) > 0) {
                outStream.write(cipher.update(buffer, 0, len));
                outStream.flush();
            }
            outStream.write(cipher.doFinal());
            byte[] byteArray = outStream.toByteArray();
            inStream.close();
            outStream.close();

            return new String(byteArray);
        } catch (IllegalBlockSizeException ex) {
            System.out.println(ex);
        } catch (BadPaddingException ex) {
            System.out.println(ex);
        } catch (InvalidKeyException ex) {
            System.out.println(ex);
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }


        return null;
    }

    public static Bitmap decryptImage(InputStream inStream) {

        try {

            Cipher cipher = Cipher.getInstance(transformation);
            SecretKeySpec secretKey = new SecretKeySpec(DecryptionHandler.getInstance().KEY.getBytes(), algorithm);
            IvParameterSpec ivSpec = new IvParameterSpec(DecryptionHandler.getInstance().IV.getBytes());

            /**
             * Initialize the cipher for decrytion
             */
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            /**
             * Initialize input and output streams
            */
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) > 0) {
                outStream.write(cipher.update(buffer, 0, len));
                outStream.flush();
            }
            outStream.write(cipher.doFinal());
            byte[] byteArray = outStream.toByteArray();
            inStream.close();
            outStream.close();

            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        } catch (IllegalBlockSizeException ex) {
            System.out.println(ex);
        } catch (BadPaddingException ex) {
            System.out.println(ex);
        } catch (InvalidKeyException ex) {
            System.out.println(ex);
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }


        return null;
    }

    private DecryptionHandler() {

        // Fetch from keystore
        String data = SecurityModule.GetData();
        if(data == null) {
            SecureRandom random = new SecureRandom();
            password = new BigInteger(130, random).toString(32);

            String url = HerokuProperties.getInstance().getUrl() + "rest/key";
            KeyLoader loader = new KeyLoader();
            loader.execute(url);
        } else {
            KEY = data.substring(0, 16);
            IV = data.substring(16, 32);
            DecryptionHandler.READY = true;
        }
    }

    private String decodeEncryptedKey(String encryptedKey){
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
                String result = decodeEncryptedKey(responseText);
                new StoreDataTask().execute(result);

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            KEY = result.substring(0, 16);
            IV = result.substring(16, 32);
            DecryptionHandler.READY = true;

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
