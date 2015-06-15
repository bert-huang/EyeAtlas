package nz.ac.aucklanduni.eyeatlas.listeners;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;

import nz.ac.aucklanduni.eyeatlas.R;
import nz.ac.aucklanduni.eyeatlas.activities.GalleryFragment;
import nz.ac.aucklanduni.eyeatlas.activities.IndexFragment;

public class NavigationDrawerListListener implements AdapterView.OnItemClickListener {
    private final Activity activity;
    private final DrawerLayout drawer;

    public NavigationDrawerListListener(Activity activity, DrawerLayout drawer) {
        this.activity = activity;
        this.drawer = drawer;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                GalleryFragment galleryFragment = new GalleryFragment();
                activity.getFragmentManager().beginTransaction().replace(R.id.fragment_container, galleryFragment, "").addToBackStack("").commit();
                drawer.closeDrawers();
                break;
            case 1:
                IndexFragment indexFragment = new IndexFragment();
                activity.getFragmentManager().beginTransaction().replace(R.id.fragment_container, indexFragment, "").addToBackStack("").commit();
                drawer.closeDrawers();
                break;
            default:
                break;
        }
    }
}
