package nz.ac.aucklanduni.eyeatlas.activities;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import nz.ac.aucklanduni.eyeatlas.R;
import nz.ac.aucklanduni.eyeatlas.adapter.ConditionAdapter;

public class GalleryFragment extends ListFragment {
    ConditionAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new ConditionAdapter<String>(getActivity(), this.getId());

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent i = new Intent(this.getActivity(), DetailedActivity.class);
        i.putExtra("CONDITION", adapter.getItem(position));
        startActivity(i);
    }
}
