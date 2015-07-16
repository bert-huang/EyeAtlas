package nz.ac.aucklanduni.eyeatlas.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Arrays;

import android.content.Context;
import android.util.Log;

import nz.ac.aucklanduni.eyeatlas.model.EyeAtlas;

public class SecurityModule {
    /**
     * Store the given serialized data onto the local device
     * @param data
     * @return
     */
    public static Boolean StoreData(String data)
    {
        if (data == null) {
            return false;
        }

        SecretKey key = CreateOrRetrieveSecretKey();
        if ( key == null )
            return false;

        try {
            WriteData(encrypt(data.getBytes("UTF-8"), key.getEncoded()), "data.ser");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Get the cryptographically securely stored data for this application
     * @return
     */
    public static String GetData()
    {
        SecretKey key = CreateOrRetrieveSecretKey();
        if ( key == null )
            return null;

        byte[] data;
        try
        {
            data = ReadData("data.ser");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

        byte[] decrypted = decrypt(data, key.getEncoded());

        if (decrypted == null) {
            return null;
        }

        return new String(decrypted);
    }

    /**
     * Encrypt the given plaintext bytes using the given key
     * @param data The plaintext to encrypt
     * @param key The key to use for encryption
     * @return The encrypted bytes
     */
    private static byte[] encrypt(byte[] data, byte[] key)
    {
        // 16 bytes is the IV size for AES256
        try
        {
            BufferedBlockCipher cipher = new BufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
            // Random iv
            SecureRandom rng = new SecureRandom();
            byte[] ivBytes = new byte[16];
            rng.nextBytes(ivBytes);

            cipher.init(true, new ParametersWithIV(new KeyParameter(key), ivBytes));
            byte[] outBuf   = new byte[cipher.getOutputSize(data.length)];

            int processed = cipher.processBytes(data, 0, data.length, outBuf, 0);
            processed += cipher.doFinal(outBuf, processed);

            byte[] outBuf2 = new byte[processed + 16];        // Make room for iv
            System.arraycopy(ivBytes, 0, outBuf2, 0, 16);    // Add iv
            System.arraycopy(outBuf, 0, outBuf2, 16, processed);    // Then the encrypted data

            return outBuf2;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Decrypt the given data with the given key
     * @param data The data to decrypt
     * @param key The key to decrypt with
     * @return The decrypted bytes
     */
    private static byte[] decrypt(byte[] data, byte[] key)
    {
        // 16 bytes is the IV size for AES256
        try
        {
            BufferedBlockCipher cipher = new BufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
            byte[] ivBytes = new byte[16];
            System.arraycopy(data, 0, ivBytes, 0, ivBytes.length); // Get iv from data
            byte[] dataonly = new byte[data.length - ivBytes.length];
            System.arraycopy(data, ivBytes.length, dataonly, 0, data.length - ivBytes.length);
            cipher.init(false, new ParametersWithIV(new KeyParameter(key), ivBytes));
            byte[] decrypted = new byte[cipher.getOutputSize(dataonly.length)];
            int len = cipher.processBytes(dataonly, 0, dataonly.length, decrypted,0);
            len += cipher.doFinal(decrypted, len);

            return decrypted;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Check for a currently saved key and if not present, create a new one
     * @return The newly or previously created key
     */
    private static SecretKey CreateOrRetrieveSecretKey()
    {
        try
        {
            byte[] keyBytes = ReadKey();
            SecretKey key;
            if ( keyBytes == null )
            {
                key = GenerateKey();
                WriteKey(key.getEncoded());
            }
            else
            {
                key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
            }
            return key;
        }
        catch( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Generate a key suitable for AES256 encryption
     * @return The generated key
     * @throws NoSuchAlgorithmException
     */
    private static SecretKey GenerateKey() throws NoSuchAlgorithmException {
        // Generate a 256-bit key
        final int outputKeyLength = 256;

        // EDIT - do not need to create SecureRandom, this is done automatically by init() if one is not provided
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    /**
     * Write the given data to private storage
     * @param data The data to store
     * @param filename The filename to store the data in
     */
    private static void WriteData(byte[] data, String filename)
    {
        FileOutputStream fOut = null;
        try {
            fOut = EyeAtlas.getAppContext().openFileOutput(filename, Context.MODE_PRIVATE);
            fOut.write(data);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the given encryption key to private storage using the hard-coded filename
     * @param key The key to write
     */
    private static void WriteKey(byte[] key)
    {
        WriteData(key, "appkey");
    }

    /**
     * Read data from private storage using the given filename
     * @param filename The filename whose contents to read
     * @return The contents of the file or null
     * @throws IOException
     */
    private static byte[] ReadData(String filename) throws IOException
    {
        byte[] key = new byte[5096];
        Arrays.fill(key, (byte)0);
        FileInputStream fOut = null;
        try
        {
            fOut = EyeAtlas.getAppContext().openFileInput(filename);
            int length = fOut.read(key);
            byte[] key2 = new byte[length];
            System.arraycopy(key, 0, key2, 0, length);
            fOut.close();
            return key2;
        }
        catch(FileNotFoundException e)
        {
            return null;
        }
    }

    /**
     * Read the encryption key from private storage
     * @return
     */
    private static byte[] ReadKey()
    {
        try
        {
            return ReadData("appkey"); // Hard-coded filename representing the encryption key
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
