package nz.ac.aucklanduni.eyeatlas.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import nz.ac.aucklanduni.eyeatlas.R;
import nz.ac.aucklanduni.eyeatlas.model.BundleKey;
import nz.ac.aucklanduni.eyeatlas.model.Condition;
import nz.ac.aucklanduni.eyeatlas.model.Properties;
import nz.ac.aucklanduni.eyeatlas.model.Tag;
import nz.ac.aucklanduni.eyeatlas.util.AsyncTaskHandler;
import nz.ac.aucklanduni.eyeatlas.util.CropBitmap;
import nz.ac.aucklanduni.eyeatlas.util.DimensionProvider;
import nz.ac.aucklanduni.eyeatlas.util.ImageLruCache;
import nz.ac.aucklanduni.eyeatlas.util.S3ImageAdapter;

public class DetailedFragment extends Fragment {
    private LinearLayout progress;
    private Condition condition;
    private FloatingActionButton fab;
    private AsyncTaskHandler asyncTaskHandler;

    @Override
    public void setArguments(Bundle bundle) {
        if (bundle != null && bundle.containsKey(BundleKey.CONDITION_KEY)) {
            this.condition = (Condition) bundle.getSerializable(BundleKey.CONDITION_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        asyncTaskHandler = new AsyncTaskHandler();

        SearchView searchView = ((MainActivity) this.getActivity()).getSearchView();
        searchView.clearFocus();

        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        progress = (LinearLayout) view.findViewById(R.id.progressBarContainer);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        final ImageView imageView = (ImageView) view.findViewById(R.id.detail_image);
        TextView title = (TextView) view.findViewById(R.id.detail_title);
        TextView description = (TextView) view.findViewById(R.id.detail_description);
        TextView category = (TextView) view.findViewById(R.id.detail_category);
        TextView tag = (TextView) view.findViewById(R.id.detail_tag);
        TextView id = (TextView) view.findViewById(R.id.detail_id);

        initialiseContent(condition, imageView, title, description, category, tag, id);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailedFragment.this.getActivity(), ImageViewerActivity.class);
                i.putExtra(BundleKey.CONDITION_KEY, condition);
                startActivity(i);
            }
        });

        return view;
    }

    private void initialiseContent(Condition condition, ImageView imageView, TextView title,
                                   TextView description, TextView category, TextView tag, TextView id) {

        final String imageKey = S3ImageAdapter.getPreviewImageUrl(condition.getId());
        Bitmap bitmap = ImageLruCache.getInstance(getActivity()).getBitmapFromCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            this.fetchImage(condition.getId(), imageView);
        }

        title.setText(condition.getTitle());
        description.setText(condition.getDescription());
        category.setText(condition.getCategory().getName());
        id.setText(condition.getId().toString());
        StringBuffer sb = new StringBuffer();

        for (Tag t : condition.getTags()) {
            sb.append(t.getName());
            sb.append(", ");
        }
        sb.delete(sb.length()-2, sb.length());
        tag.setText(sb.toString());
    }

    private void fetchImage(final int id, final ImageView imageView) {
        new AsyncTask<Object, Object, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Object... params) {
                Bitmap bmp = null;
                try {
                    bmp = S3ImageAdapter.getPreviewImage(id, Properties.getInstance(DetailedFragment.this.getActivity()));
                    if (bmp != null) {
                        ImageLruCache.getInstance(DetailedFragment.this.getActivity()).addBitmapToCache(S3ImageAdapter.getPreviewImageUrl(id), bmp);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bmp;
            }

            @Override
            protected void onPreExecute() {
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(Bitmap result) {

                if (result == null) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(DetailedFragment.this.getActivity());
                    alert.setTitle("Error");
                    alert.setMessage("Cannot fetch image from server. Please check your " +
                            "internet connection or contact administrator for support.");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    fab.setEnabled(false);

                    result = BitmapFactory.decodeResource(
                            DetailedFragment.this.getActivity().getResources(), R.drawable.ic_404);
                    imageView.setImageBitmap(result);
                } else {
                    imageView.setImageBitmap(result);
                }


                progress.setVisibility(View.GONE);
            }
        }.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SearchView searchView = ((MainActivity) this.getActivity()).getSearchView();
        if(!searchView.isIconified()) {
            searchView.setQuery("", false);
            searchView.clearFocus();
            searchView.setIconified(true);
        }

        if (asyncTaskHandler != null) {
            asyncTaskHandler.purgeAll();
        }
    }
}
