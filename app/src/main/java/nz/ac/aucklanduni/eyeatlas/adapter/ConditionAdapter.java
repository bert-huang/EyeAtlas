package nz.ac.aucklanduni.eyeatlas.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import nz.ac.aucklanduni.eyeatlas.R;
import nz.ac.aucklanduni.eyeatlas.model.Condition;
import nz.ac.aucklanduni.eyeatlas.model.Properties;
import nz.ac.aucklanduni.eyeatlas.util.S3ImageAdapter;

public class ConditionAdapter extends ArrayAdapter<Condition>  {

    public ConditionAdapter(Activity activity, int viewId, List<Condition> items) {
        super(activity, viewId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflator.inflate(R.layout.condition_item, parent, false);
        int color = R.color.transblack;

        LinearLayout progressCont = (LinearLayout) rowView.findViewById(R.id.progressBarContainer);
        TextView textView = (TextView) rowView.findViewById(R.id.cardText);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.cardImage);
        textView.setBackgroundColor(color);

        textView.setText(getItem(position).getTitle());
        fetchImage(getItem(position).getId(), imageView, progressCont);

        return rowView;
    }

    private void fetchImage(final Integer id, final ImageView view, final LinearLayout progress) {
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected void onPreExecute() {
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            protected Bitmap doInBackground(Void... avoid) {
                Bitmap bmp;
                try {
                    bmp = S3ImageAdapter.getThumbnail(id, Properties.getInstance(ConditionAdapter.this.getContext()));
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return bmp;
            }

            @Override
            protected void onPostExecute(Bitmap bmp) {
                progress.setVisibility(View.GONE);
                if (bmp == null) {
                    bmp = BitmapFactory.decodeResource(
                            ConditionAdapter.this.getContext().getResources(), R.drawable.ic_404);
                }
                view.setImageBitmap(bmp);
            }
        }.execute();
    }
}