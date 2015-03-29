package nz.ac.aucklanduni.eyeatlas.util;


import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileReader {

    public static String getFileContent(String filePath, Context context) {

        InputStream in = null;
        StringBuffer out = new StringBuffer();

        try {
            in =  fileStreamProvider(filePath, context);
            int c;
            while((c = in.read()) != -1) {
                out.append((char)c);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return out.toString();
    }

    static InputStream fileStreamProvider(String filePath, Context context) throws IOException {
        return context.getAssets().open(filePath);
    }

}
