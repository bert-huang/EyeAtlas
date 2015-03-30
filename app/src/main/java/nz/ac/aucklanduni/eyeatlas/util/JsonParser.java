package nz.ac.aucklanduni.eyeatlas.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class JsonParser {

    public static JSONObject getJsonObject(String filePath, Context context) throws JSONException {
        JSONObject jObject = new JSONObject(FileReader.getFileContent(filePath, context));
        return jObject;
    }
}