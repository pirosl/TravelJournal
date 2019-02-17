package com.lucianpiros.traveljournal.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.DataCache;
import com.lucianpiros.traveljournal.data.FirebaseDB;
import com.lucianpiros.traveljournal.model.Note;
import com.lucianpiros.traveljournal.service.UpdateNoteService;
import com.lucianpiros.traveljournal.ui.util.UIUtility;
import com.lucianpiros.traveljournal.ui.widget.MovieAlertDialog;
import com.lucianpiros.traveljournal.ui.widget.PhotoAlertDialog;
import com.lucianpiros.traveljournal.ui.widget.ProgressBarTask;

import java.text.SimpleDateFormat;

import butterknife.OnClick;

/**
 * Edit note activity. Extends @EditableJournalNoteActivity and implements @UpdateNoteService.UpdateNoteServiceListener interface.
 * Used to edit and save notes
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class EditJournalNoteActivity extends EditableJournalNoteActivity implements UpdateNoteService.UpdateNoteServiceListener {

    private static final String TAG = EditJournalNoteActivity.class.getSimpleName();

    // Note to be updated
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            String noteKey = bundle.getString(getResources().getString(R.string.noteactivity_extra_param));

            note = DataCache.getInstance().getNote(noteKey);

            noteTitleET.setText(note.getNoteTitle());
            noteContentET.setText(note.getNoteContent());

            SimpleDateFormat dateSF = new SimpleDateFormat("d MMM yyyy");
            noteDateTV.setText(dateSF.format(note.getNoteCreationDate()));

            String photoURL = note.getPhotoDownloadURL();
            if (photoURL != null && photoURL.length() > 0) {
                notePictureBT.setVisibility(View.VISIBLE);
            }

            String moviewURL = note.getMovieDownloadURL();
            if (moviewURL != null && moviewURL.length() > 0) {
                noteMovieBT.setVisibility(View.VISIBLE);
            }
        }

        UpdateNoteService.getInstance().setUpdateNoteServiceListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_save:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Save note
     */
    private void saveNote() {
        if (UIUtility.isValid(noteTitleET) && UIUtility.isValid(noteContentET)) {
            progressBarTask = new ProgressBarTask(progressBarHolder, this);
            progressBarTask.execute();

            UpdateNoteService.getInstance().setContentResolver(getContentResolver());
            note.setNoteTitle(noteTitleET.getText().toString());
            note.setNoteContent(noteContentET.getText().toString());

            UpdateNoteService.getInstance().updateNote(note);
        } else {
            Snackbar snackbar = Snackbar
                    .make(mainLayout, getResources().getString(R.string.notetitleorcontent_empty), Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    @Override
    public void onComplete() {
        progressBarTask.cancel(true);
        Snackbar snackbar = Snackbar
                .make(mainLayout, getResources().getString(R.string.savenote_success), Snackbar.LENGTH_SHORT);

        /*if (!success) {
            snackbar.setText(getResources().getString(R.string.addnote_error));
        }*/

        snackbar.show();

        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        annimateAddFAB();

        if ((requestCode == PICK_PHOTO || requestCode == TAKE_PHOTO)
                && resultCode == RESULT_OK
                && null != data) {
            Uri selectedPhotoUri = data.getData();
            UpdateNoteService.getInstance().setSelectedPhotoUri(selectedPhotoUri);
            notePictureBT.setVisibility(View.VISIBLE);
        }
        if (requestCode == PICK_VIDEO && resultCode == RESULT_OK
                && null != data) {
            Uri selectedVideoUri = data.getData();
            UpdateNoteService.getInstance().setSelectedVideoUri(selectedVideoUri);
            noteMovieBT.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.note_picture_btn)
    protected void showImage() {
        PhotoAlertDialog photoDialog = new PhotoAlertDialog(this.getLayoutInflater(), this);
        photoDialog.initialize(viewGroup);

        if (UpdateNoteService.getInstance().getSelectedPhotoUri() != null) {
            photoDialog.showLocal(UpdateNoteService.getInstance().getSelectedPhotoUri());
        } else {
            photoDialog.showRemote(note.getPhotoDownloadURL());
        }
    }

    @OnClick(R.id.note_movie_btn)
    protected void showMovie() {
        MovieAlertDialog movieDialog = new MovieAlertDialog(this.getLayoutInflater(), this);
        movieDialog.initialize(viewGroup);

        if (UpdateNoteService.getInstance().getSelectedVideoUri() != null) {
            movieDialog.showLocal(UpdateNoteService.getInstance().getSelectedVideoUri());
        } else {
            movieDialog.showRemote(note.getNoteKey(), note.getMovieDownloadURL());
        }
    }
}
