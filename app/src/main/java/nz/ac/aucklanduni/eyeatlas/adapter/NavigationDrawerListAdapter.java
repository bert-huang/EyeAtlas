package nz.ac.aucklanduni.eyeatlas.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import nz.ac.aucklanduni.eyeatlas.R;
import nz.ac.aucklanduni.eyeatlas.activities.GalleryFragment;

public class NavigationDrawerListAdapter extends ArrayAdapter {

    private static final int COUNT = 2;
    private static final int ICONS[] = {R.drawable.ic_photo, R.drawable.ic_list};
    private static final int NAMES[] = {R.string.gallery, R.string.index};


    public NavigationDrawerListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflator.inflate(R.layout.item_row, parent, false);

        ImageView imgView = (ImageView) rowView.findViewById(R.id.rowIcon);
        TextView txtView = (TextView) rowView.findViewById(R.id.rowText);

        imgView.setImageResource(ICONS[position]);
        txtView.setText(NAMES[position]);

        return rowView;
    }

    @Override
    public int getCount() {
        return NavigationDrawerListAdapter.COUNT;
    }
}
