package nz.ac.aucklanduni.eyeatlas.model;

import android.app.Application;
import android.content.Context;

public class EyeAtlas extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        EyeAtlas.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return EyeAtlas.context;
    }
}