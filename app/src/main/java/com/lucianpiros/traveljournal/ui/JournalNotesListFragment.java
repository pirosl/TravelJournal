package com.lucianpiros.traveljournal.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.FirebaseDB;
import com.lucianpiros.traveljournal.data.adapter.NotesAdapter;
import com.lucianpiros.traveljournal.model.Note;

import java.util.List;

public class JournalNotesListFragment extends Fragment implements FirebaseDB.NoteDBEventsListener {

    @BindView(R.id.notesrecyclerview) RecyclerView noteRV;

    @BindView(R.id.floatingactionbutton) FloatingActionButton fabButton;

    public JournalNotesListFragment() {

    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_journalnoteslist, container, false);

        ButterKnife.bind(this, fragmentView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(fragmentView.getContext());
        noteRV.setLayoutManager(mLayoutManager);
        noteRV.setItemAnimator(new DefaultItemAnimator());

        FirebaseDB.getInstance().setNoteDBEventsListener(this);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note note = new Note();
                note.setNoteContent("Content");
                note.setNoteTitle("Title");

                FirebaseDB.getInstance().save(note);
            }
        });
        return fragmentView;
    }

    @Override
    public void OnNotesListChanged(List<Note> notesList) {
        NotesAdapter adapter = new NotesAdapter(notesList);
        noteRV.setAdapter(adapter);
    }
}
