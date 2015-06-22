package nz.ac.aucklanduni.eyeatlas.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
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
import nz.ac.aucklanduni.eyeatlas.model.Condition;
import nz.ac.aucklanduni.eyeatlas.model.Properties;

public class GalleryFragment extends Fragment {

    private static Integer INTERVAL = 10;
    private ConditionAdapter adapter;
    private List<Condition> list;
    private GridView grid;
    private LinearLayout progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.gallery_fragment, container, false);
        grid = (GridView) view.findViewById(R.id.grid);
        progress = (LinearLayout) view.findViewById(R.id.progressBarContainer);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        list = new ArrayList<>();

        ConditionLoader task = new ConditionLoader();
        String url = Properties.getInstance(this.getActivity()).getHerokuUrl() + "rest/condition/all/";
        Log.w("XEYE", url);
        task.execute(url);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(GalleryFragment.this.getActivity(), DetailedActivity.class);
                i.putExtra("CONDITION", adapter.getItem(position));
                startActivity(i);
            }
        });

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
            adapter = new ConditionAdapter(getActivity(), GalleryFragment.this.getId(), GalleryFragment.this.list);
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
