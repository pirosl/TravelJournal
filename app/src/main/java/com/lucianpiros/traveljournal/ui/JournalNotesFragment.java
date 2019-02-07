package com.lucianpiros.traveljournal.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.adapter.NotesAdapter;

public class JournalNotesFragment extends Fragment implements JournalNotesListFragment.OnItemSelectedListener {

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

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_journalnotes, journalNotesListFragment, "")
                .commit();

        return fragmentView;
    }

    @Override
    public void onItemSelected(int noteIdx) {
        Intent intent = new Intent(this.getContext(), NoteActivity.class);
        intent.putExtra(getResources().getString(R.string.noteactivity_extra_param), noteIdx);
        startActivity(intent);
    }
}