package nz.ac.aucklanduni.eyeatlas.activities;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

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

import nz.ac.aucklanduni.eyeatlas.adapter.ConditionAdapter;
import nz.ac.aucklanduni.eyeatlas.model.Condition;
import nz.ac.aucklanduni.eyeatlas.model.Properties;

public class GalleryFragment extends ListFragment {

    private static Integer INTERVAL = 10;
    private ConditionAdapter adapter;
    private List<Condition> list;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        list = new ArrayList<>();
        ConditionLoader task = new ConditionLoader();
        String url = Properties.getInstance(this.getActivity()).getHerokuUrl() + "rest/condition/all/";
        task.execute(url);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent i = new Intent(this.getActivity(), DetailedActivity.class);
        i.putExtra("CONDITION", adapter.getItem(position));
        startActivity(i);
    }

    private class ConditionLoader extends AsyncTask<String, Void, List<Condition>> {

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
            GalleryFragment.this.setListAdapter(adapter);

        }
    }
}
