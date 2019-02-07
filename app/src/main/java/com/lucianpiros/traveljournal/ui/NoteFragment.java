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
import com.lucianpiros.traveljournal.service.AddNoteService;
import com.lucianpiros.traveljournal.ui.widget.PhotoAlertDialog;

import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
  //  @BindView(android.R.id.content)
    private ViewGroup viewGroup;

    private LayoutInflater layoutInflater;
    private Note note;

    public NoteFragment() {
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.layoutInflater = inflater;
        this.viewGroup = container;

        View noteView = inflater.inflate(R.layout.fragment_note, container, false);

        ButterKnife.bind(this, noteView);

        // Retrieve recipe idx passed as parameter from Main Activity
        Bundle bundle = getArguments();

        int noteIdx = -1;
        if(bundle != null)
            noteIdx = bundle.getInt(getResources()
                    .getString(R.string.noteactivity_extra_param));

        note = FirebaseDB.getInstance().getNote(noteIdx);

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

    @OnClick(R.id.note_picture_btn)
    protected void showImage() {
        PhotoAlertDialog photoDialog = new PhotoAlertDialog(layoutInflater, this.getContext());
        photoDialog.initialize(viewGroup);
        photoDialog.showRemote(note.getPhotoDownloadURL());
    }
}
