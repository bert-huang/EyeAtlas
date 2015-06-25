package nz.ac.aucklanduni.eyeatlas.activities;

import android.graphics.Bitmap;
import android.net.http.HttpResponseCache;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialiseDrawer();

        ConditionFragment conditionFragment = new ConditionFragment();
        this.getFragmentManager().beginTransaction().replace(R.id.fragment_container, conditionFragment).addToBackStack(null).commit();
    }

    public SearchView getSearchView() {
        return searchView;
    }

    private void initialiseSearch(Menu menu) {
        SearchView actionSearch = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView = actionSearch;
        actionSearch.setSubmitButtonEnabled(true);
        actionSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s != null && !s.equals("")) {
                    ConditionFragment conditionFragment = new ConditionFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(BundleKey.SEARCH_KEY, s);
                    conditionFragment.setArguments(bundle);
                    MainActivity.this.getFragmentManager().beginTransaction().replace(R.id.fragment_container, conditionFragment).addToBackStack(null).commit();
                    return true;
                } else {
                    return false;
                }
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
            searchView.setQuery("", false);
            searchView.setIconified(true);
        } else if (getFragmentManager().getBackStackEntryCount() > 1 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}
