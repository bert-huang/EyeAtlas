package nz.ac.aucklanduni.eyeatlas.activities;

import android.app.ListFragment;
import android.os.Bundle;
import nz.ac.aucklanduni.eyeatlas.adapter.ConditionAdapter;

public class GalleryFragment extends ListFragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(new ConditionAdapter<String>(getActivity(), this.getId()));
    }
}
