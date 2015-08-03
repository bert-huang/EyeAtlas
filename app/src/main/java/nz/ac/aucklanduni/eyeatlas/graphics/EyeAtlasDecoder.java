package nz.ac.aucklanduni.eyeatlas.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.qozix.tileview.graphics.BitmapDecoder;

import nz.ac.aucklanduni.eyeatlas.model.S3Properties;
import nz.ac.aucklanduni.eyeatlas.util.S3ImageAdapter;

public class EyeAtlasDecoder implements BitmapDecoder {

    private static final BitmapFactory.Options OPTIONS = new BitmapFactory.Options();
    static {
        OPTIONS.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    @Override
    public Bitmap decode(String fileName, Context context) {
        return  S3ImageAdapter.getTile(fileName, S3Properties.getInstance(context));
    }
}
