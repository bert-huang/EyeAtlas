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

import java.io.Serializable;
import java.util.List;

import nz.ac.aucklanduni.eyeatlas.R;
import nz.ac.aucklanduni.eyeatlas.model.Condition;
import nz.ac.aucklanduni.eyeatlas.model.Properties;
import nz.ac.aucklanduni.eyeatlas.util.S3ImageAdapter;

public class ConditionAdapter extends ArrayAdapter<Condition> implements Serializable {

    private LruCache<String, Bitmap> mMemoryCache;

    public ConditionAdapter(Activity activity, int viewId, List<Condition> items) {
        super(activity, viewId, items);

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
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

        final String imageKey = String.valueOf(id);

        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            view.imageView.setImageBitmap(bitmap);
        } else {
            FetchImageTask task = new FetchImageTask(id, view);
            task.execute();
        }
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
                bmp = S3ImageAdapter.getThumbnail(id, Properties.getInstance(ConditionAdapter.this.getContext()));
                addBitmapToMemoryCache(String.valueOf(id), bmp);
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

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }



}