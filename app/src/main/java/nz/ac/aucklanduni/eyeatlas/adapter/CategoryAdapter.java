package nz.ac.aucklanduni.eyeatlas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import nz.ac.aucklanduni.eyeatlas.R;

public class CategoryAdapter<T> extends ArrayAdapter {
    public CategoryAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflator.inflate(R.layout.category_item, parent, false);

        return rowView;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
