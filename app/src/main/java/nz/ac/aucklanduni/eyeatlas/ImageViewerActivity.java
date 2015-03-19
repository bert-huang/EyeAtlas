package nz.ac.aucklanduni.eyeatlas;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.qozix.tileview.TileView;

import java.io.IOException;
import java.io.InputStream;


public class ImageViewerActivity extends Activity {

    private TileView tileView;
    private static final int IMAGE_SCALE = 0;

    //bundle variables passed to activity
    private int imageId;
    private String imageName;
    private int imageSizeX;
    private int imageSizeY;
    private float[] detailLevels;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.unpackBundle(savedInstanceState);

        tileView = new TileView( this );
        setContentView( tileView );

        tileView.setBackgroundColor(Color.BLACK);

        // size of original image at 100% scale
        tileView.setSize(imageSizeX, imageSizeY );

        // detail levels
        tileView.addDetailLevel( 0.500f, "tiles/fantasy/500/%col%_%row%.jpg", "samples/middle-earth.jpg");
        tileView.addDetailLevel( 0.250f, "tiles/fantasy/250/%col%_%row%.jpg", "samples/middle-earth.jpg");
        tileView.addDetailLevel( 0.125f, "tiles/fantasy/125/%col%_%row%.jpg", "samples/middle-earth.jpg");

        // allow scaling past original size
        tileView.setScaleLimits( 0, 2 );

        // frame the troll
        frameTo( imageSizeX / 2, imageSizeY / 2 );

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

    private void unpackBundle(Bundle bundle) {
        imageSizeY = 4057;
        imageSizeX = 4015;
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
