package nz.ac.aucklanduni.eyeatlas.activities;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import nz.ac.aucklanduni.eyeatlas.R;
import nz.ac.aucklanduni.eyeatlas.adapter.CategoryAdapter;

public class IndexFragment extends ListFragment {
    CategoryAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new CategoryAdapter<String>(getActivity(), this.getId());

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        IndexFragment nextFrag= new IndexFragment();
        this.getFragmentManager().beginTransaction().replace(R.id.fragment_container, nextFrag, "").addToBackStack(null).commit();
    }
}
