package nz.ac.aucklanduni.eyeatlas.activities;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import nz.ac.aucklanduni.eyeatlas.R;
import nz.ac.aucklanduni.eyeatlas.adapter.CategoryAdapter;
import nz.ac.aucklanduni.eyeatlas.model.Category;
import nz.ac.aucklanduni.eyeatlas.model.Properties;

public class IndexFragment extends ListFragment {
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    public final static String CATEGORY_KEY = "Category";

    @Override
    public void setArguments(Bundle bundle) {
        if (bundle != null && bundle.containsKey(CATEGORY_KEY)) {
            this.categoryList = (List<Category>) bundle.getSerializable(CATEGORY_KEY);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new CategoryAdapter<String>(getActivity(), this.getId());

        setListAdapter(adapter);
        this.setCategories();
    }

    private void setCategories() {
        if (categoryList != null) {
            adapter.setCategories(categoryList);
            return;
        }
        new AsyncTask<Object, Object, List<Category>>() {
            @Override
            protected List<Category> doInBackground(Object... params) {
                try {
                    URL url = new URL(Properties.getInstance(IndexFragment.this.getActivity()).getHerokuUrl() + "rest/category");
                    URLConnection conn = url.openConnection();
                    InputStream in = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String val = br.readLine().toString();

                    ObjectMapper ob = new ObjectMapper ();
                    return Arrays.asList(ob.readValue(val, Category[].class));
                } catch (Exception e) {
                    e.printStackTrace();
                    //Display popup and shut down the app
                    throw new RuntimeException("Condition count could not be obtained");
                }
            }

            @Override
            protected void onPostExecute(List<Category> result) {
                categoryList = result;
                adapter.setCategories(categoryList);
            }
        }.execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        List<Category> children = categoryList.get(position).getChildren();

        if (children.isEmpty()) {
            GalleryFragment galleryFragment = new GalleryFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(CATEGORY_KEY, categoryList.get(position));
            galleryFragment.setArguments(bundle);
            this.getFragmentManager().beginTransaction().replace(R.id.fragment_container, galleryFragment, "").addToBackStack(null).commit();
            return;
        }

        IndexFragment indexFragment = new IndexFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CATEGORY_KEY, (java.io.Serializable) children);
        indexFragment.setArguments(bundle);
        this.getFragmentManager().beginTransaction().replace(R.id.fragment_container, indexFragment, "").addToBackStack(null).commit();
    }
}
