package com.lucianpiros.traveljournal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucianpiros.traveljournal.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Adventures fragment. Displays a list of adventures
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class AdventuresFragment extends Fragment {

    public AdventuresFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_adventures, container, false);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_adventures, new AdventuresListFragment())
                .commit();

        return fragmentView;
    }
}
