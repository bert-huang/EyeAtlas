package nz.ac.aucklanduni.eyeatlas.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import nz.ac.aucklanduni.eyeatlas.R;
import nz.ac.aucklanduni.eyeatlas.adapter.NavigationDrawerListAdapter;
import nz.ac.aucklanduni.eyeatlas.model.Condition;
import nz.ac.aucklanduni.eyeatlas.model.Properties;
import nz.ac.aucklanduni.eyeatlas.util.S3ImageAdapter;

public class DetailedActivity extends AppCompatActivity {
    private Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationDrawerListAdapter adapter = new NavigationDrawerListAdapter(this, R.id.NavigationDrawerList);
        ListView listView = (ListView) findViewById(R.id.NavigationDrawerList);
        listView.setAdapter(adapter);
        listView.setClickable(true);

        initialiseDrawer();

        ImageView imageView = (ImageView) findViewById(R.id.detail_image);
        TextView title = (TextView) findViewById(R.id.detail_title);
        TextView detail = (TextView) findViewById(R.id.detail_detail);

        Bundle extras = getIntent().getExtras();
        final Condition condition = (Condition) extras.get("CONDITION");

        initialiseContent(condition ,imageView, title, detail);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailedActivity.this, ImageViewerActivity.class);
                i.putExtra("CONDITION", condition);
                startActivity(i);
            }
        });
    }

    private void initialiseContent(Condition condition, ImageView imageView, TextView title, TextView detail) {
        title.setText(condition.getTitle());
        detail.setText(condition.getDescription());
        this.setImage(condition.getId(), imageView);
    }

    private void setImage(final int id, final ImageView imageView) {
        new AsyncTask<Object, Object, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Object... params) {
                Bitmap bmp;
                try {
                    bmp = S3ImageAdapter.getDetailImage(id, Properties.getInstance(DetailedActivity.this));
                } catch (Exception e) {
                    e.printStackTrace();
                    //Display popup and shut down the app
                    throw new RuntimeException("Image could not be obtained");
                }
                return bmp;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                imageView.setImageBitmap(result);
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initialiseDrawer() {

        drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer,toolbar,R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

        };
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }
}
