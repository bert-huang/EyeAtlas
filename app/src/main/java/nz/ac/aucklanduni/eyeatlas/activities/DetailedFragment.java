package nz.ac.aucklanduni.eyeatlas.activities;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import nz.ac.aucklanduni.eyeatlas.R;
import nz.ac.aucklanduni.eyeatlas.model.Condition;
import nz.ac.aucklanduni.eyeatlas.model.Properties;
import nz.ac.aucklanduni.eyeatlas.model.Tag;
import nz.ac.aucklanduni.eyeatlas.util.S3ImageAdapter;

public class DetailedFragment extends Fragment {
    private LinearLayout progress;
    public static String BUNDLE_KEY = "CONDITION";
    private Condition condition;

    @Override
    public void setArguments(Bundle bundle) {
        if (bundle != null && bundle.containsKey(BUNDLE_KEY)) {
            this.condition = (Condition) bundle.getSerializable(BUNDLE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        progress = (LinearLayout) view.findViewById(R.id.progressBarContainer);

        ImageView imageView = (ImageView) view.findViewById(R.id.detail_image);
        TextView title = (TextView) view.findViewById(R.id.detail_title);
        TextView description = (TextView) view.findViewById(R.id.detail_description);
        TextView category = (TextView) view.findViewById(R.id.detail_category);
        TextView tag = (TextView) view.findViewById(R.id.detail_tag);
        TextView id = (TextView) view.findViewById(R.id.detail_id);

        initialiseContent(condition ,imageView, title, description, category, tag, id);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailedFragment.this.getActivity(), ImageViewerActivity.class);
                i.putExtra(BUNDLE_KEY, condition);
                startActivity(i);
            }
        });

        return view;
    }

    private void initialiseContent(Condition condition, ImageView imageView, TextView title, TextView description, TextView category, TextView tag, TextView id) {

        this.setImage(condition.getId(), imageView);

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

    private void setImage(final int id, final ImageView imageView) {
        new AsyncTask<Object, Object, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Object... params) {
                Bitmap bmp;
                try {
                    bmp = S3ImageAdapter.getDetailImage(id, Properties.getInstance(DetailedFragment.this.getActivity()));
                } catch (Exception e) {
                    e.printStackTrace();
                    //Display popup and shut down the app
                    throw new RuntimeException("Image could not be obtained");
                }
                return bmp;
            }

            @Override
            protected void onPreExecute() {
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                imageView.setImageBitmap(result);
                progress.setVisibility(View.GONE);
            }
        }.execute();
    }
}
