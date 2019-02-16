package com.lucianpiros.traveljournal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucianpiros.traveljournal.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * Calendar container fragment. Displays a calendar and for selected days will push a fragment with list of notes.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class CalendarContainerFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_calendarcontainer, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.app_name));


        getFragmentManager().beginTransaction().replace(R.id.calendarcontainer, new CalendarFragment()).commit();

        return view;
    }
}
