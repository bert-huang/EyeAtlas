package nz.ac.aucklanduni.eyeatlas.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import nz.ac.aucklanduni.eyeatlas.R;
import nz.ac.aucklanduni.eyeatlas.activities.GalleryFragment;
import nz.ac.aucklanduni.eyeatlas.activities.IndexFragment;
import nz.ac.aucklanduni.eyeatlas.model.BundleKey;
import nz.ac.aucklanduni.eyeatlas.model.Category;
import nz.ac.aucklanduni.eyeatlas.model.Condition;

public class CategoryAdapter extends ArrayAdapter<Category> {

    Activity activity;

    public CategoryAdapter(Activity activity, int viewId, List<Category> items) {
        super(activity, viewId, items);
        this.activity = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolderItem viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.category_item, parent, false);

            viewHolder = new ViewHolderItem();
            viewHolder.infoBtn = (Button) convertView.findViewById(R.id.infoBtn);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.text);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        viewHolder.textView.setText(getItem(position).getName());
        viewHolder.infoBtn.setOnClickListener(new OnCategoryInfoClick(position));
        convertView.setOnClickListener(new OnCategoryItemClick(position));

        return convertView;
    }

    /**
     * A View template to avoid multiple view inflation when getting view
     */
    static class ViewHolderItem {
        TextView textView;
        Button infoBtn;
    }

    class OnCategoryInfoClick implements View.OnClickListener {

        private int position;

        public OnCategoryInfoClick(int position) {
            this.position = position;

        }

        @Override
        public void onClick(View v) {

            Dialog dialog = new Dialog(CategoryAdapter.this.activity);
            dialog.setTitle(CategoryAdapter.this.getItem(position).getName());

            AlertDialog.Builder alert = new AlertDialog.Builder(CategoryAdapter.this.activity);
            alert.setTitle(CategoryAdapter.this.getItem(position).getName());
            alert.setMessage(CategoryAdapter.this.getItem(position).getDescription());
            alert.setPositiveButton("Dismiss", null);
            alert.show();
        }
    }

    class OnCategoryItemClick implements View.OnClickListener {

        private int position;

        public OnCategoryItemClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            List<Category> children = CategoryAdapter.this.getItem(position).getChildren();

            if (children.isEmpty()) {
                GalleryFragment galleryFragment = new GalleryFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(BundleKey.CATEGORY_KEY, CategoryAdapter.this.getItem(position));
                galleryFragment.setArguments(bundle);
                CategoryAdapter.this.activity.getFragmentManager().beginTransaction().replace(R.id.fragment_container, galleryFragment).addToBackStack(null).commit();
                return;
            }

            IndexFragment indexFragment = new IndexFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(BundleKey.CATEGORY_KEY, (java.io.Serializable) children);
            indexFragment.setArguments(bundle);
            CategoryAdapter.this.activity.getFragmentManager().beginTransaction().replace(R.id.fragment_container, indexFragment).addToBackStack(null).commit();
        }
    }
}