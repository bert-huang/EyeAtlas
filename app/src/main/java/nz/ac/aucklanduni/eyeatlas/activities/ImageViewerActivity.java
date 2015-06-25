package nz.ac.aucklanduni.eyeatlas.activities;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.qozix.tileview.TileView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import nz.ac.aucklanduni.eyeatlas.graphics.EyeAtlasDecoder;
import nz.ac.aucklanduni.eyeatlas.model.BundleKey;
import nz.ac.aucklanduni.eyeatlas.model.Condition;


public class ImageViewerActivity extends Activity {

    private TileView tileView;
    private static final float IMAGE_SCALE = 0;

    private int imageSizeX;
    private int imageSizeY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getIntent().getExtras();
        Condition condition = (Condition) bundle.getSerializable(BundleKey.CONDITION_KEY);

        imageSizeY = condition.getImageHeight();
        imageSizeX = condition.getImageWidth();

        tileView = new TileView( this );
        tileView.setCacheEnabled( true );
        setContentView( tileView );
        tileView.setBackgroundColor(Color.BLACK);
        //tileView.disableSuppress();
        tileView.setDecoder(new EyeAtlasDecoder());

        // size of original image at 100% scale
        tileView.setSize(imageSizeX, imageSizeY );

        String previewFile = condition.getId().toString() + "/preview/preview.jpg";


        // detail levels
        tileView.addDetailLevel( 1.000f, condition.getId().toString() + "/1000/img_%col%_%row%.jpg", previewFile, 2000, 2000);
        tileView.addDetailLevel( 0.750f, condition.getId().toString() + "/500/img_%col%_%row%.jpg", previewFile, 2000, 2000);
        tileView.addDetailLevel( 0.500f, condition.getId().toString() + "/250/img_%col%_%row%.jpg", previewFile, 2000, 2000);
        tileView.addDetailLevel( 0.250f, condition.getId().toString() + "/125/img_%col%_%row%.jpg", previewFile, 2000, 2000);

        // allow scaling past original size
        tileView.setScaleLimits( 0, 2 );

        frameTo(imageSizeX / 2, imageSizeY / 2);

        // scale down a little
        tileView.setScale( IMAGE_SCALE );

    }

    @Override
    public void onPause() {
        super.onPause();
        tileView.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        tileView.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tileView.destroy();
        tileView = null;
    }

    public TileView getTileView(){
        return tileView;
    }

    /**
     * This is a convenience method to moveToAndCenter after layout (which won't happen if called directly in onCreate
     * see https://github.com/moagrius/TileView/wiki/FAQ
     */
    private void frameTo( final double x, final double y ) {
        getTileView().post( new Runnable() {
            @Override
            public void run() {
                getTileView().moveToAndCenter( x, y );
            }
        });
    }
}
