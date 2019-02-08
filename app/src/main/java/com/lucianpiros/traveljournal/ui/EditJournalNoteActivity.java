package com.lucianpiros.traveljournal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.FirebaseDB;
import com.lucianpiros.traveljournal.model.Note;
import com.lucianpiros.traveljournal.service.AddNoteService;
import com.lucianpiros.traveljournal.ui.widget.ProgressBarTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import androidx.core.os.ConfigurationCompat;

public class EditJournalNoteActivity extends EditableJournalNoteActivity {

    private static final String TAG = EditJournalNoteActivity.class.getSimpleName();

    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            String noteKey = bundle.getString(getResources().getString(R.string.noteactivity_extra_param));

            note = FirebaseDB.getInstance().getNote(noteKey);


            noteTitleET.setText(note.getNoteTitle());
            noteContentET.setText(note.getNoteContent());

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

        }
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

    private void saveNote() {
        if (isValid(noteTitleET) && isValid(noteContentET)) {
            progressBarTask = new ProgressBarTask(progressBarHolder, this);
            progressBarTask.execute();

            //AddNoteService.getInstance().setContentResolver(getContentResolver());
            //AddNoteService.getInstance().setNoteTitle(noteTitleET.getText().toString());
            //AddNoteService.getInstance().setNoteContent(noteContentET.getText().toString());

//            AddNoteService.getInstance().addNote();
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
}
