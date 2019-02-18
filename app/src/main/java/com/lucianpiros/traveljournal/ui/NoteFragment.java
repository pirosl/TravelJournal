package com.lucianpiros.traveljournal.ui;

import android.content.Intent;
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
import com.lucianpiros.traveljournal.data.DataCache;
import com.lucianpiros.traveljournal.model.Note;
import com.lucianpiros.traveljournal.service.DeleteNoteService;
import com.lucianpiros.traveljournal.ui.util.UIUtility;
import com.lucianpiros.traveljournal.ui.widget.ConfirmationDialog;
import com.lucianpiros.traveljournal.ui.widget.MovieAlertDialog;
import com.lucianpiros.traveljournal.ui.widget.PhotoAlertDialog;
import com.lucianpiros.traveljournal.ui.widget.ProgressBarTask;

import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Fragment displaying a single note. Handle note edit and note delete requests.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class NoteFragment extends Fragment implements DeleteNoteService.DeleteNoteServiceListener, ConfirmationDialog.ConfirmationDialogActionListener {

    private final static String TAG = NoteFragment.class.getSimpleName();

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
    private String noteKey;

    /**
     * Class constructor
     */
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

        noteKey = null;
        if (bundle != null)
            noteKey = bundle.getString(getResources()
                    .getString(R.string.noteactivity_extra_param));

        return noteView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (noteKey != null) {
            note = DataCache.getInstance().getNote(noteKey);

            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(note.getNoteTitle());
            noteContentTV.setText(note.getNoteContent());

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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.viewnote_menu, menu);

        // change tint color
        UIUtility.changeTintColor(menu, R.id.action_edit, getContext());
        UIUtility.changeTintColor(menu, R.id.action_delete, getContext());
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (UIUtility.getViewPager().getCurrentItem() == 0) {
            menu.findItem(R.id.action_edit).setVisible(true);
            menu.findItem(R.id.action_delete).setVisible(true);
        } else {
            menu.findItem(R.id.action_edit).setVisible(false);
            menu.findItem(R.id.action_delete).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_edit:
                editNote();
                return true;
            case R.id.action_delete:
                deleteNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Delete note
     */
    private void deleteNote() {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(layoutInflater, getContext());
        confirmationDialog.setConfirmationDialogActionListener(this);
        confirmationDialog.initialize(viewGroup, getString(R.string.deletenote_confirmationdialogtitle));
        confirmationDialog.show();
    }

    /**
     * Edit note. Launch edit note activity
     */
    private void editNote() {
        Intent intent = new Intent(this.getContext(), EditJournalNoteActivity.class);
        intent.putExtra(getResources().getString(R.string.noteactivity_extra_param), note.getNoteKey());
        startActivity(intent);
    }

    @OnClick(R.id.note_picture_btn)
    protected void showImage() {
        PhotoAlertDialog photoDialog = new PhotoAlertDialog(layoutInflater, this.getContext());
        photoDialog.initialize(viewGroup);
        photoDialog.showRemote(note.getPhotoDownloadURL());
    }

    @OnClick(R.id.note_movie_btn)
    protected void showMovie() {
        MovieAlertDialog movieDialog = new MovieAlertDialog(layoutInflater, this.getContext());
        movieDialog.initialize(viewGroup);
        movieDialog.showRemote(note.getNoteKey(), note.getMovieFileName());
    }

    @Override
    public void onComplete() {
        progressBarTask.cancel(true);
        if(getActivity() != null) {
            Snackbar snackbar = Snackbar
                    .make(mainLayout, getActivity().getApplication().getResources().getString(R.string.deletenote_success), Snackbar.LENGTH_SHORT);

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
