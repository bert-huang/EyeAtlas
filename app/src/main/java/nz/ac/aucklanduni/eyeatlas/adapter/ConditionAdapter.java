package nz.ac.aucklanduni.eyeatlas.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
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

    /**
     * A View template to avoid multiple view inflation when getting view
     */
    static class ViewHolderItem {
        TextView textView;
        ImageView imageView;
        LinearLayout progress;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.condition_item, parent, false);

            viewHolder = new ViewHolderItem();
            viewHolder.progress = (LinearLayout) convertView.findViewById(R.id.progressBarContainer);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.cardText);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.cardImage);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        viewHolder.textView.setText(getItem(position).getTitle());

        // Clear the image then load from cache/remote
        viewHolder.imageView.setImageResource(android.R.color.transparent);
        fetchImage(getItem(position).getId(), viewHolder);

        return convertView;
    }

    private void fetchImage(final Integer id, final ViewHolderItem view) {
        FetchImageTask task = new FetchImageTask(id, view);
        task.execute();
    }

    class FetchImageTask extends AsyncTask<Void, Void, Bitmap> {

        private Integer id;
        private ViewHolderItem view;

        public FetchImageTask(Integer id, ViewHolderItem view) {
            this.id = id;
            this.view = view;
        }

        @Override
        protected void onPreExecute() {
            view.progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Void... avoid) {
            Bitmap bmp;
            try {
                bmp = S3ImageAdapter.getThumbnail(id, Properties.getInstance(ConditionAdapter.this.getContext()), ConditionAdapter.this.getContext());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            view.progress.setVisibility(View.GONE);
            if (bmp == null) {
                bmp = BitmapFactory.decodeResource(
                        ConditionAdapter.this.getContext().getResources(), R.drawable.ic_404);
            }
            view.imageView.setImageBitmap(bmp);
        }
    }
}