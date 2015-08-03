package nz.ac.aucklanduni.eyeatlas.model;

import android.content.Context;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

import nz.ac.aucklanduni.eyeatlas.util.FileReader;

public class HerokuProperties {

    private static HerokuProperties instance;
    private static final String FILE_PATH = "heroku.properties";
    private String url;

    public static HerokuProperties getInstance() {
        if (instance == null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                instance = mapper.readValue(FileReader.fileStreamProvider(FILE_PATH, EyeAtlas.getAppContext()), HerokuProperties.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String herokuUrl) {
        this.url = herokuUrl;
    }
}
