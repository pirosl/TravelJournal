package com.lucianpiros.traveljournal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucianpiros.traveljournal.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Fragment displaying a list of notes. If master-detail note detail will be displayed allongside list of notes
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class JournalNotesFragment extends Fragment implements JournalNotesListFragment.OnItemSelectedListener {

    // is this a master-detail fragment
    private boolean mMasterDetailFlow;

    /**
     * Class constructor
     */
    public JournalNotesFragment() {

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
        View fragmentView = inflater.inflate(R.layout.fragment_journalnotes, container, false);

        JournalNotesListFragment journalNotesListFragment = new JournalNotesListFragment();//(JournalNotesListFragment) getFragmentManager().findFragmentById(R.id.fragment_journalnotes);
        journalNotesListFragment.setOnItemSelectedListener(this);

        Bundle arguments = getArguments();
        if(arguments != null) {
            journalNotesListFragment.setArguments(arguments);
        }

        assert getFragmentManager() != null;
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_journalnotes, journalNotesListFragment)
                .commit();

        if (fragmentView.findViewById(R.id.note_fragment) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in master details mode.
            mMasterDetailFlow = true;
            if (savedInstanceState == null) {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.note_fragment, new NoteFragment())
                        .commit();
            }
        } else {
            mMasterDetailFlow = false;
        }

        return fragmentView;
    }

    @Override
    public void onItemSelected(int noteIdx) {
        if (mMasterDetailFlow) {
            Bundle arguments = new Bundle();
            arguments.putInt(getResources().getString(R.string.noteactivity_extra_param), noteIdx);

            NoteFragment noteFragment = new NoteFragment();
            noteFragment.setArguments(arguments);

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.note_fragment, noteFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this.getContext(), NoteActivity.class);
            intent.putExtra(getResources().getString(R.string.noteactivity_extra_param), noteIdx);
            startActivity(intent);
        }
    }
}