package nz.ac.aucklanduni.eyeatlas.util;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonObjectWalker {

    public String getString(JSONObject obj, String ... varargs) throws JSONException {
        JSONObject curObj = obj;
        for(int i=0; i < varargs.length - 1; i ++) {
            curObj = (JSONObject)curObj.get(varargs[i]);
        }
        return curObj.getString(varargs[varargs.length-1]);
    }
}
