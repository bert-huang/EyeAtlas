package nz.ac.aucklanduni.eyeatlas.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import nz.ac.aucklanduni.eyeatlas.R;
import nz.ac.aucklanduni.eyeatlas.adapter.ConditionAdapter;
import nz.ac.aucklanduni.eyeatlas.model.BundleKey;
import nz.ac.aucklanduni.eyeatlas.model.Category;
import nz.ac.aucklanduni.eyeatlas.model.Condition;
import nz.ac.aucklanduni.eyeatlas.model.Properties;
import nz.ac.aucklanduni.eyeatlas.util.AsyncTaskHandler;

public class GalleryFragment extends Fragment {

    private static Integer INTERVAL = 10;
    private ConditionAdapter adapter;
    private List<Condition> list;
    private GridView grid;
    private LinearLayout progress;

    private AsyncTaskHandler asyncTaskHandler;
    private String url;
    private String term;

    @Override
    public void setArguments(Bundle bundle) {
        if (bundle != null) {
            if (bundle.containsKey(BundleKey.CATEGORY_KEY)) {
                Category category = (Category) bundle.getSerializable(BundleKey.CATEGORY_KEY);
                url = Properties.getInstance(this.getActivity()).getHerokuUrl() + "rest/condition/category/" + category.getId().replaceAll(" ", "%20");
            } else if (bundle.containsKey(BundleKey.SEARCH_KEY)) {
                term = (String) bundle.getSerializable(BundleKey.SEARCH_KEY);
                url = Properties.getInstance(this.getActivity()).getHerokuUrl() + "/rest/condition/search/" + term.replaceAll(" ", "%20");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.gallery_fragment, container, false);
        grid = (GridView) view.findViewById(R.id.grid);
        progress = (LinearLayout) view.findViewById(R.id.progressBarContainer);
        return view;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BundleKey.SEARCH_KEY, term);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SearchView searchView = ((MainActivity) this.getActivity()).getSearchView();
        if (searchView != null) {
            if (term != null) {
                searchView.setQuery(term, false);
            } else {
                searchView.setQuery("", false);
            }
            searchView.clearFocus();
        }

        list = new ArrayList<>();
        asyncTaskHandler = new AsyncTaskHandler();

        ConditionLoader task = new ConditionLoader();

        if (url == null) {
            url = Properties.getInstance(this.getActivity()).getHerokuUrl() + "rest/condition/all/";
        }

        asyncTaskHandler.add(task);
        task.execute(url);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DetailedFragment detailedFragment = new DetailedFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(BundleKey.CONDITION_KEY, adapter.getItem(position));
                detailedFragment.setArguments(bundle);
                GalleryFragment.this.getFragmentManager().beginTransaction().replace(R.id.fragment_container, detailedFragment).addToBackStack(null).commit();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (asyncTaskHandler != null) {
            asyncTaskHandler.purgeAll();
        }
    }

    private class ConditionLoader extends AsyncTask<String, Void, List<Condition>> {

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Condition> doInBackground(String... urls) {

            List<Condition> list;

            try {
                URL url = new URL(urls[0]);
                URLConnection conn = url.openConnection();
                InputStream in = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String val = br.readLine();

                ObjectMapper mapper = new ObjectMapper ();
                JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Condition.class);
                list = mapper.readValue(val, type);
            } catch (IOException e) {
                e.printStackTrace();
                return null;

            }
            return list;
        }

        @Override
        protected void onPostExecute(List<Condition> list) {

            if (list == null) {
                list = new ArrayList<>();
                AlertDialog.Builder alert = new AlertDialog.Builder(GalleryFragment.this.getActivity());
                alert.setTitle("Error");
                alert.setMessage("Cannot contact remote server. Please try again later.");
                alert.setPositiveButton("OK", null);
                alert.show();
            }

            GalleryFragment.this.list = list;
            adapter = new ConditionAdapter(getActivity(), getId(), GalleryFragment.this.list);
            GalleryFragment.this.grid.setAdapter(adapter);

            // Place a tiny delay for the view to be inflated (0.5 second)
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress.setVisibility(View.GONE);
                }
            }, 500);
        }
    }

}
