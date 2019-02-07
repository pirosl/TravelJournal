package com.lucianpiros.traveljournal.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.FirebaseDB;
import com.lucianpiros.traveljournal.model.Note;
import com.lucianpiros.traveljournal.service.DeleteNoteService;
import com.lucianpiros.traveljournal.ui.widget.ConfirmationDialog;
import com.lucianpiros.traveljournal.ui.widget.PhotoAlertDialog;
import com.lucianpiros.traveljournal.ui.widget.ProgressBarTask;

import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoteFragment extends Fragment implements DeleteNoteService.DeleteNoteServiceListener, ConfirmationDialog.ConfirmationDialogActionListener {

    @BindView(R.id.viewnote_mainlayout)
    CoordinatorLayout mainLayout;
    @BindView(R.id.note_date)
    TextView noteDateTV;
    @BindView(R.id.note_picture_btn)
    ImageButton notePictureBT;
    @BindView(R.id.note_movie_btn)
    ImageButton noteMovieBT;
    @BindView(R.id.note_content)
    TextView noteContentTV;
    @BindView(R.id.progressbarholder)
    FrameLayout progressBarHolder;

    private ViewGroup viewGroup;
    private LayoutInflater layoutInflater;
    private Note note;
    private ProgressBarTask progressBarTask;

    public NoteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(note.getNoteTitle());
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.viewnote_menu, menu);

        // change tint color
        changeTintColor(menu, R.id.action_edit);
        changeTintColor(menu, R.id.action_delete);
    }

    private void changeTintColor(Menu menu, int menuRid) {
        Drawable drawable = menu.findItem(menuRid).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(getContext(), R.color.colorAccent));
        menu.findItem(menuRid).setIcon(drawable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteNote() {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(layoutInflater, getContext());
        confirmationDialog.setConfirmationDialogActionListener(this);
        confirmationDialog.initialize(viewGroup, getString(R.string.deletenote_confirmationdialogtitle));
        confirmationDialog.show();
    }

    @OnClick(R.id.note_picture_btn)
    protected void showImage() {
        PhotoAlertDialog photoDialog = new PhotoAlertDialog(layoutInflater, this.getContext());
        photoDialog.initialize(viewGroup);
        photoDialog.showRemote(note.getPhotoDownloadURL());
    }

    @Override
    public void onComplete() {
        progressBarTask.cancel(true);
        Snackbar snackbar = Snackbar
                .make(mainLayout, getResources().getString(R.string.deletenote_success), Snackbar.LENGTH_SHORT);

        /*if (!success) {
            snackbar.setText(getResources().getString(R.string.addnote_error));
        }*/

        snackbar.show();

        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onAccept() {
        progressBarTask = new ProgressBarTask(progressBarHolder, getActivity());
        progressBarTask.execute();

        DeleteNoteService.getInstance().setDeleteNoteServiceListener(this);
        DeleteNoteService.getInstance().deleteNote(note);
    }

    @Override
    public void onDecline() {

    }
}
