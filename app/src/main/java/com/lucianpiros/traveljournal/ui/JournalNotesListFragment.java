package com.lucianpiros.traveljournal.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucianpiros.traveljournal.R;

public class JournalNotesListFragment extends Fragment {

    public JournalNotesListFragment() {

    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_journalnoteslist, container, false);

        return fragmentView;
    }
}
