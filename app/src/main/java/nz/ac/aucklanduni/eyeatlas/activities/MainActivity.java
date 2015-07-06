package nz.ac.aucklanduni.eyeatlas.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.http.HttpResponseCache;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import nz.ac.aucklanduni.eyeatlas.R;
import nz.ac.aucklanduni.eyeatlas.adapter.NavigationDrawerListAdapter;
import nz.ac.aucklanduni.eyeatlas.listeners.NavigationDrawerListListener;
import nz.ac.aucklanduni.eyeatlas.model.BundleKey;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle mDrawerToggle;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        try {
            File httpCacheDir = new File(this.getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i("XEYE", "HTTP response cache installation failed:" + e);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialiseDrawer();

        ConditionFragment conditionFragment = new ConditionFragment();
        String fTag = Integer.toString(MainActivity.this.getFragmentManager().getBackStackEntryCount());
        this.getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, conditionFragment, fTag).addToBackStack(fTag).commit();
    }

    public SearchView getSearchView() {
        return searchView;
    }

    private void initialiseSearch(Menu menu) {
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search");
        // text color
        AutoCompleteTextView searchText = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchText, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {}

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                ConditionFragment conditionFragment = new ConditionFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(BundleKey.SEARCH_KEY, s);
                conditionFragment.setArguments(bundle);

                String fTag = Integer.toString(MainActivity.this.getFragmentManager().getBackStackEntryCount());
                FragmentTransaction tx = MainActivity.this.getFragmentManager().beginTransaction();
                tx.setCustomAnimations(R.animator.slide_right_enter, R.animator.slide_left_exit, R.animator.slide_left_enter, R.animator.slide_right_exit);
                tx.replace(R.id.fragment_container, conditionFragment, fTag).addToBackStack(fTag);
                tx.commit();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        initialiseSearch(menu);
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

        NavigationDrawerListAdapter adapter = new NavigationDrawerListAdapter(this, R.id.NavigationDrawerList);
        NavigationDrawerListListener listener = new NavigationDrawerListListener(this, drawer);
        ListView listView = (ListView) findViewById(R.id.NavigationDrawerList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);
        listView.setClickable(true);
    }

    @Override
    public void onBackPressed() {
        if(!searchView.isIconified()) {
            searchView.setIconified(true);
            searchView.setQuery("", false);

            // Get the last fragment, if it is a condition fragment and main role is to display search results, then pop the search fragment
            FragmentManager.BackStackEntry bse = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount()-1);
            Fragment fragment = getFragmentManager().findFragmentByTag(bse.getName());
            if(fragment instanceof ConditionFragment) {
                ConditionFragment conditionFragment = (ConditionFragment) fragment;
                if (conditionFragment.getRole() == ConditionFragment.Role.SEARCH) {
                    getFragmentManager().popBackStack();
                }
            }

        } else if (getFragmentManager().getBackStackEntryCount() > 1 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}
