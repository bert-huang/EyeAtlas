package nz.ac.aucklanduni.eyeatlas.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import nz.ac.aucklanduni.eyeatlas.R;
import nz.ac.aucklanduni.eyeatlas.model.Condition;
import nz.ac.aucklanduni.eyeatlas.model.Properties;
import nz.ac.aucklanduni.eyeatlas.util.S3ImageAdapter;

public class ConditionAdapter<T> extends ArrayAdapter {

    private Condition[] conditions;
    private Integer count;

    public ConditionAdapter(Activity activity, int viewId) {
        super(activity, viewId);
        conditions = new Condition[this.getCount()];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflator.inflate(R.layout.condition_item, parent, false);
        int color = R.color.transblack;

        TextView textView = (TextView) rowView.findViewById(R.id.cardText);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.cardImage);
        initialiseCondition(position, textView, imageView);
        textView.setBackgroundColor(color);

        return rowView;
    }

    private void initialiseCondition(final int position, final TextView textView, final ImageView imageView) {
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (conditions[position] == null) {
                    ConditionAdapter.this.getConditionNames(position);
                }

                String title = conditions[position].getTitle();
                return title;
            }

            @Override
            protected void onPostExecute(String result) {
                textView.setText(result);
                ConditionAdapter.this.setImage(imageView, result);
            }
        }.execute();
    }

    private void getConditionNames(final int position) {
        try {
            int interval = 50;
            int startIndex = position - position%interval;
            int endIndex = (startIndex + interval > getCount() ? getCount() : startIndex + interval);

            URL url = new URL(Properties.getInstance(ConditionAdapter.this.getContext()).getHerokuUrl() + "rest/condition/all/" + startIndex + "/" + endIndex);
            URLConnection conn = url.openConnection();
            InputStream in = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String val = br.readLine().toString();

            ObjectMapper ob = new ObjectMapper ();
            conditions = ob.readValue(val, Condition[].class);

            for(int i = 0; i < conditions.length; i++) {
                this.conditions[startIndex + i] = conditions[i];
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Display popup and shut down the app
            throw new RuntimeException("Image could not be obtained");
        }
    }

    private void setImage(final ImageView imageView, final String name) {
        new AsyncTask<Object, Object, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Object... params) {
                Bitmap bmp;
                try {
                    bmp = S3ImageAdapter.getThumbnail(name, Properties.getInstance(ConditionAdapter.this.getContext()));
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
    public synchronized int getCount() {
        if (this.count == null) {
            loadCount();
        }
        return count;
    }

    private void loadCount() {
        AsyncTask task = new AsyncTask<Object, Object, Integer>() {
            @Override
            protected Integer doInBackground(Object... params) {
                Integer count;
                try {
                    URL url = new URL(Properties.getInstance(ConditionAdapter.this.getContext()).getHerokuUrl() + "rest/condition/all/count");
                    URLConnection conn = url.openConnection();
                    InputStream in = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String val = br.readLine().toString();
                    count = Integer.parseInt(val);
                } catch (Exception e) {
                    e.printStackTrace();
                    //Display popup and shut down the app
                    throw new RuntimeException("Condition count could not be obtained");
                }
                return count;
            }
        }.execute();

        try {
            ConditionAdapter.this.count = (Integer) task.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            //Display popup and shut down the app
            throw new RuntimeException("Condition count could not be obtained");
        }
    }
}