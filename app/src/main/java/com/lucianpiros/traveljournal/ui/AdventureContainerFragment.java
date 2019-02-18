package com.lucianpiros.traveljournal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.DataCache;
import com.lucianpiros.traveljournal.model.Adventure;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class AdventureContainerFragment extends Fragment {

    private AdventuresFragment adventuresFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_adventurecontainer, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        adventuresFragment = new AdventuresFragment();
        getFragmentManager().beginTransaction().replace(R.id.adventurecontainer, adventuresFragment).commit();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            adventuresFragment.initialClick();
        }
    }
}
