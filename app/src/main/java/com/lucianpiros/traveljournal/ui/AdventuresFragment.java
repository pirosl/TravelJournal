package com.lucianpiros.traveljournal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.DataCache;
import com.lucianpiros.traveljournal.data.adapter.AdapterFilter;
import com.lucianpiros.traveljournal.model.Adventure;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Adventures fragment. Displays a list of adventures
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class AdventuresFragment extends Fragment implements AdventuresListFragment.OnItemSelectedListener {

    // is this a master-detail fragment
    private boolean mMasterDetailFlow;

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

        AdventuresListFragment adventuresListFragment = new AdventuresListFragment();
        adventuresListFragment.setOnItemSelectedListener(this);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_adventures, adventuresListFragment)
                .commit();

        if (fragmentView.findViewById(R.id.adventure_fragment) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in master details mode.
            mMasterDetailFlow = true;
            if (savedInstanceState == null) {
                AdventureFragment adventureFragment = new AdventureFragment();

                getChildFragmentManager().beginTransaction()
                        .replace(R.id.adventure_fragment, adventureFragment)
                        .commit();
            }
        } else {
            mMasterDetailFlow = false;
        }

        return fragmentView;
    }

    @Override
    public void onItemSelected(String adventureKey) {
        if (mMasterDetailFlow) {
            AdventureFragment adventureFragment = new AdventureFragment();
            Bundle arguments = new Bundle();
            arguments.putString(getResources().getString(R.string.noteslistactivity_adventurekey), adventureKey);

            adventureFragment.setArguments(arguments);

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.adventure_fragment, adventureFragment)
                    .commit();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            List<Adventure> adventuresList = DataCache.getInstance().getAdventuresList();
            if (adventuresList != null && adventuresList.size() > 0) {
                onInitialItemSelected(adventuresList.get(0).getAdventureKey());
            }
        }
    }

    @Override
    public void onInitialItemSelected(String adventureKey) {
        if (mMasterDetailFlow) {
            AdventureFragment adventureFragment = new AdventureFragment();
            Bundle arguments = new Bundle();
            arguments.putString(getResources().getString(R.string.noteslistactivity_adventurekey), adventureKey);

            adventureFragment.setArguments(arguments);

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.adventure_fragment, adventureFragment)
                    .commit();
        }
    }
}
