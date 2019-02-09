package com.lucianpiros.traveljournal.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.service.AddNoteService;
import com.lucianpiros.traveljournal.ui.widget.ProgressBarTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.ConfigurationCompat;

public class AddJournalNoteActivity extends EditableJournalNoteActivity implements AddNoteService.AddNoteServiceListener {

    private static final String TAG = AddJournalNoteActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // set not date and time
            Date noteCreationDate = new Date();

            AddNoteService.getInstance().setNoteCreationDate(noteCreationDate);

            SimpleDateFormat dateSF = new SimpleDateFormat(getString(R.string.note_dateformat), ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0));
            noteDateTV.setText(dateSF.format(noteCreationDate));

            getSupportActionBar().setTitle(getString(R.string.addnote_title));
            // testing purpose
            noteTitleET.setText("Title " + new Random().nextInt(1000));
            noteContentET.setText("Lorem ipsum dolor sit amet, quo nisl ubique ut. Latine delectus comprehensam ex vis. Id per noluisse reformidans, labore eripuit eleifend ut per. Vel cu sint quodsi alterum. Habeo euismod ad duo.\n" +
                    "\n" +
                    "Consul aliquam mea id, nullam primis vim ut. Eu sea dico iisque assueverit. Vim et meis errem eleifend. Ex reque verterem nam, in novum zril solet eum. An mei odio disputando, eos sanctus vocibus euripidis cu, nominavi philosophia mei no. Ex corpora antiopam oportere pri, an has paulo viderer.");
            // end testing
        }

        AddNoteService.getInstance().setAddNoteServiceListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_save:
                addNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addNote() {
        if (isValid(noteTitleET) && isValid(noteContentET)) {
            progressBarTask = new ProgressBarTask(progressBarHolder, this);
            progressBarTask.execute();

            AddNoteService.getInstance().setContentResolver(getContentResolver());
            AddNoteService.getInstance().setNoteTitle(noteTitleET.getText().toString());
            AddNoteService.getInstance().setNoteContent(noteContentET.getText().toString());

            AddNoteService.getInstance().addNote();
        } else {
            Snackbar snackbar = Snackbar
                    .make(mainLayout, getResources().getString(R.string.notetitleorcontent_empty), Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        annimateAddFAB();

        if ((requestCode == PICK_PHOTO || requestCode == TAKE_PHOTO)
                && resultCode == RESULT_OK
                && null != data) {
            Uri selectedPhotoUri = data.getData();
            AddNoteService.getInstance().setSelectedPhotoUri(selectedPhotoUri);
            notePictureBT.setVisibility(View.VISIBLE);
        }
        if (requestCode == PICK_VIDEO && resultCode == RESULT_OK
                && null != data) {
            Uri selectedVideoUri = data.getData();
            AddNoteService.getInstance().setSelectedVideoUri(selectedVideoUri);
            noteMovieBT.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onComplete() {
        progressBarTask.cancel(true);
        Snackbar snackbar = Snackbar
                .make(mainLayout, getResources().getString(R.string.addnote_success), Snackbar.LENGTH_SHORT);

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