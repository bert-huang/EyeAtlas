package nz.ac.aucklanduni.eyeatlas.activities;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nz.ac.aucklanduni.eyeatlas.R;
import nz.ac.aucklanduni.eyeatlas.adapter.CategoryAdapter;
import nz.ac.aucklanduni.eyeatlas.adapter.ConditionAdapter;
import nz.ac.aucklanduni.eyeatlas.model.Category;
import nz.ac.aucklanduni.eyeatlas.model.Condition;
import nz.ac.aucklanduni.eyeatlas.model.Properties;
import nz.ac.aucklanduni.eyeatlas.util.AsyncTaskHandler;

public class IndexFragment extends Fragment {
    private CategoryAdapter adapter;
    private List<Category> list;
    public final static String CATEGORY_KEY = "Category";
    private LinearLayout progress;
    private ListView listView;

    private AsyncTaskHandler asyncTaskHandler;

    @Override
    public void setArguments(Bundle bundle) {
        if (bundle != null && bundle.containsKey(CATEGORY_KEY)) {
            this.list = (List<Category>) bundle.getSerializable(CATEGORY_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.index_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        progress = (LinearLayout) view.findViewById(R.id.progressBarContainer);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        asyncTaskHandler = new AsyncTaskHandler();

        if(list != null) {
            adapter = new CategoryAdapter(this.getActivity(), this.getId(), list);
            listView.setAdapter(adapter);
        } else {
            CategoryLoader task = new CategoryLoader();
            String url = Properties.getInstance(IndexFragment.this.getActivity()).getHerokuUrl() + "rest/category";
            Log.w("XEYE", url);
            asyncTaskHandler.add(task);
            task.execute(url);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        asyncTaskHandler.purgeAll();

    }

    class CategoryLoader extends AsyncTask<String, Void, List<Category>> {

        @Override
        protected void onPreExecute() {
            IndexFragment.this.progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Category> doInBackground(String... urls) {

            List<Category> list;

            try {
                URL url = new URL(urls[0]);
                URLConnection conn = url.openConnection();
                InputStream in = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String val = br.readLine();

                ObjectMapper mapper = new ObjectMapper ();
                JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Category.class);
                list = mapper.readValue(val, type);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return list;
        }



        @Override
        protected void onPostExecute(List<Category> list) {
            if (list == null) {
                list = new ArrayList<>();
            }

            IndexFragment.this.list = list;
            adapter = new CategoryAdapter(IndexFragment.this.getActivity(), IndexFragment.this.getId(), IndexFragment.this.list);
            IndexFragment.this.listView.setAdapter(adapter);

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

    class CategoryClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.w("XEYE", "clicked position " + position);
            List<Category> children = list.get(position).getChildren();

            if (children.isEmpty()) {
                Log.w("XEYE", "children is empty");
                GalleryFragment galleryFragment = new GalleryFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(CATEGORY_KEY, list.get(position));
                galleryFragment.setArguments(bundle);
                IndexFragment.this.getFragmentManager().beginTransaction().replace(R.id.fragment_container, galleryFragment, "").addToBackStack(null).commit();
                return;
            }

            IndexFragment indexFragment = new IndexFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(CATEGORY_KEY, (java.io.Serializable) children);
            indexFragment.setArguments(bundle);
            IndexFragment.this.getFragmentManager().beginTransaction().replace(R.id.fragment_container, indexFragment, "").addToBackStack(null).commit();
        }
    }
}
