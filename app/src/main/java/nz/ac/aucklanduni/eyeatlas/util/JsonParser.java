package nz.ac.aucklanduni.eyeatlas.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;


public class JsonParser {

    public static void jsonToMap(String filePath, Context context) throws JSONException {


        HashMap<String, String> map = new HashMap<String, String>();
        JSONObject jObject = new JSONObject(FileReader.getFileContent(filePath, context));
        Iterator<?> keys = jObject.keys();

        while( keys.hasNext() ){
            String key = (String)keys.next();
            String value = jObject.getString(key);
            map.put(key, value);

        }

        System.out.println("json : "+jObject);
        System.out.println("map : "+map);
    }
}
