package com.lucianpiros.traveljournal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.FirebaseDB;
import com.lucianpiros.traveljournal.model.Note;

import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteFragment extends Fragment {

    @BindView(R.id.note_title)
    TextView noteTitleTV;
    @BindView(R.id.note_date)
    TextView noteDateTV;
    @BindView(R.id.note_picture_btn)
    ImageButton notePictureBT;
    @BindView(R.id.note_movie_btn)
    ImageButton noteMovieBT;
    @BindView(R.id.note_content)
    TextView noteContentTV;

    public NoteFragment() {
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View noteView = inflater.inflate(R.layout.fragment_note, container, false);

        ButterKnife.bind(this, noteView);

        // Retrieve recipe idx passed as parameter from Main Activity
        Bundle bundle = getArguments();

        int noteIdx = -1;
        if(bundle != null)
            noteIdx = bundle.getInt(getResources()
                    .getString(R.string.noteactivity_extra_param));

        Note note = FirebaseDB.getInstance().getNote(noteIdx);

        noteTitleTV.setText(note.getNoteTitle());
        noteContentTV.setText(note.getNoteContent());

        SimpleDateFormat dateSF = new SimpleDateFormat("d MMM yyyy");
        noteDateTV.setText(dateSF.format(note.getNoteCreationDate()));

        String photoURL = note.getPhotoDownloadURL();
        if(photoURL != null && photoURL.length() > 0) {
            notePictureBT.setVisibility(View.VISIBLE);
        }

        String moviewURL = note.getMovieDownloadURL();
        if(moviewURL != null && moviewURL.length() > 0) {
            noteMovieBT.setVisibility(View.VISIBLE);
        }

        return noteView;
    }
}
