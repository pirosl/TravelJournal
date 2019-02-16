package com.lucianpiros.traveljournal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucianpiros.traveljournal.R;

import androidx.fragment.app.Fragment;

/**
 * Map container fragment. Displays a map and for selected days will push a fragment with list of notes.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class MapContainerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_mapcontainer, container, false);

        getFragmentManager().beginTransaction().replace(R.id.mapcontainer, new MapFragment()).commit();

        return view;
    }
}
