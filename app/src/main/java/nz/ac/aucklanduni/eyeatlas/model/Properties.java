package nz.ac.aucklanduni.eyeatlas.model;

import android.content.Context;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

import nz.ac.aucklanduni.eyeatlas.util.FileReader;

public class Properties {
    private static Properties instance;
    private static final String FILE_PATH = ".properties";

    public static Properties getInstance(Context context) {
        if (instance == null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                instance = mapper.readValue(FileReader.getFileContent(FILE_PATH, context), Properties.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
