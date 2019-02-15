package com.lucianpiros.traveljournal.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.annotations.NotNull;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.service.AddNoteService;
import com.lucianpiros.traveljournal.ui.widget.CustomAlertDialog;
import com.lucianpiros.traveljournal.ui.widget.MovieAlertDialog;
import com.lucianpiros.traveljournal.ui.widget.PhotoAlertDialog;
import com.lucianpiros.traveljournal.ui.widget.ProgressBarTask;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Represents an editable note activitty.
 * Used to add and edit a note.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public abstract class EditableJournalNoteActivity extends AppCompatActivity implements CustomAlertDialog.CustomDialogActionListener {

    private static final String TAG = EditableJournalNoteActivity.class.getSimpleName();

    protected static final int PHOTO = 1;
    protected static final int VIDEO = 2;
    protected static final int PICK_PHOTO = 1;
    protected static final int PICK_VIDEO = 2;
    protected static final int TAKE_PHOTO = 3;
    protected static final int TAKE_VIDEO = 4;

    @BindView(R.id.addnote_mainlayout)
    CoordinatorLayout mainLayout;
    @BindView(R.id.fab_addpicture)
    FloatingActionButton addPictureFAB;
    @BindView(R.id.fab_addmovie)
    FloatingActionButton addMovieFAB;
    @BindView(R.id.fab_add)
    FloatingActionButton addFAB;
    @BindView(R.id.note_title)
    EditText noteTitleET;
    @BindView(R.id.note_date)
    TextView noteDateTV;
    @BindView(R.id.note_picture_btn)
    ImageButton notePictureBT;
    @BindView(R.id.note_movie_btn)
    ImageButton noteMovieBT;
    @BindView(R.id.progressbarholder)
    FrameLayout progressBarHolder;
    @BindView(R.id.note_content)
    EditText noteContentET;

    @BindView(android.R.id.content)
    ViewGroup viewGroup;

    private boolean isAddFABExpanded;

    // animations used for FloatingActionButtons
    private Animation expandFABAnimation, collapseFABAnimation, closeFABAnimation, openFABAnimation;

    // ProgressBar to be displayed when add / save operation takes place.
    protected ProgressBarTask progressBarTask;

    protected String mCurrentPhotoPath;
    protected String mCurrentVideoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addjournalnote);

        ButterKnife.bind(this);

        isAddFABExpanded = false;
        expandFABAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_expand);
        collapseFABAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_colapse);
        closeFABAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_close);
        openFABAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_open);

        if (savedInstanceState != null) {
            noteTitleET.setText(savedInstanceState.getString(getString(R.string.addnotestate_title)));
            noteContentET.setText(savedInstanceState.getString(getString(R.string.addnotestate_body)));
            noteDateTV.setText(savedInstanceState.getString(getString(R.string.addnotestate_date)));
            notePictureBT.setVisibility(savedInstanceState.getInt(getString(R.string.addnotestate_picture)));
            noteMovieBT.setVisibility(savedInstanceState.getInt(getString(R.string.addnotestate_movie)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putString(getString(R.string.addnotestate_title), noteTitleET.getText().toString());
        saveInstanceState.putString(getString(R.string.addnotestate_body), noteContentET.getText().toString());
        saveInstanceState.putString(getString(R.string.addnotestate_date), noteDateTV.getText().toString());
        saveInstanceState.putInt(getString(R.string.addnotestate_picture), notePictureBT.getVisibility());
        saveInstanceState.putInt(getString(R.string.addnotestate_movie), noteMovieBT.getVisibility());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.addnote_menu, menu);

        // change tint color
        Drawable drawable = menu.findItem(R.id.action_save).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorAccent));
        menu.findItem(R.id.action_save).setIcon(drawable);

        return true;
    }

    /**
     * Returns true if passed in TextView contains information
     *
     * @param textView - TextView to be checked
     * @return - true if TextView contains info, false otherwise
     */
    protected boolean isValid(@NotNull TextView textView) {
        CharSequence text = textView.getText();

        return (text != null && text.toString().length() > 0);
    }

    @OnClick(R.id.fab_add)
    protected void annimateAddFAB() {
        if (isAddFABExpanded) {
            addFAB.startAnimation(closeFABAnimation);

            addPictureFAB.startAnimation(collapseFABAnimation);
            addPictureFAB.setClickable(false);

            addMovieFAB.startAnimation(collapseFABAnimation);
            addMovieFAB.setClickable(false);

            isAddFABExpanded = false;
        } else {
            addFAB.startAnimation(openFABAnimation);

            addPictureFAB.startAnimation(expandFABAnimation);
            addPictureFAB.setClickable(true);

            addMovieFAB.startAnimation(expandFABAnimation);
            addMovieFAB.setClickable(true);

            isAddFABExpanded = true;
        }
    }

    @OnClick(R.id.fab_addpicture)
    protected void addPicture() {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(PHOTO, this.getLayoutInflater(), this);
        customAlertDialog.setCustomDialogActionListener(this);
        customAlertDialog.initialize(viewGroup, R.string.add_photo, R.string.add_photo_option1, R.string.add_photo_option2, R.string.add_photo_option3);
        customAlertDialog.show();
    }

    @OnClick(R.id.fab_addmovie)
    protected void addMovie() {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(VIDEO, this.getLayoutInflater(), this);
        customAlertDialog.setCustomDialogActionListener(this);
        customAlertDialog.initialize(viewGroup, R.string.add_video, R.string.add_video_option1, R.string.add_video_option2, R.string.add_video_option3);
        customAlertDialog.show();
    }

    @OnClick(R.id.note_picture_btn)
    protected void showImage() {
        PhotoAlertDialog photoDialog = new PhotoAlertDialog(this.getLayoutInflater(), this);
        photoDialog.initialize(viewGroup);
        photoDialog.showLocal(AddNoteService.getInstance().getSelectedPhotoUri());
    }

    @OnClick(R.id.note_movie_btn)
    protected void showMovie() {
        MovieAlertDialog movieDialog = new MovieAlertDialog(this.getLayoutInflater(), this);
        movieDialog.initialize(viewGroup);
        movieDialog.showLocal(AddNoteService.getInstance().getSelectedVideoUri());
    }

    @Override
    public void onOption1(int dialogType) {
        if (dialogType == PHOTO) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.d(TAG, "Error creating temporary photo file " + ex.getMessage());
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            getString(R.string.fileprovider_authority),
                            photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, TAKE_PHOTO);
                }
            }
        }
        if (dialogType == VIDEO) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File videoFile = null;
                try {
                    videoFile = createVideoFile();
                } catch (IOException ex) {
                    Log.d(TAG, "Error creating temporary photo file " + ex.getMessage());
                }
                if (videoFile != null) {
                    Uri videoURI = FileProvider.getUriForFile(this,
                            getString(R.string.fileprovider_authority),
                            videoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                    startActivityForResult(takePictureIntent, TAKE_VIDEO);
                }
            }
        }

    }

    /**
     * Creates a temporary image file
     *
     * @return newly created file
     * @throws IOException if file can't be created
     */
    private File createImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                getString(R.string.photo_tmpfilename),
                getString(R.string.photo_extension),
                storageDir
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Creates a temporary video file
     *
     * @return newly created file
     * @throws IOException if file can't be created
     */
    private File createVideoFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File image = File.createTempFile(
                getString(R.string.video_tmpfilename),
                getString(R.string.video_extension),
                storageDir
        );
        mCurrentVideoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onOption2(int dialogType) {
        if (dialogType == PHOTO) {
            Intent takephotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
            takephotoIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(takephotoIntent, PICK_PHOTO);
        }
        if (dialogType == VIDEO) {
            Intent takevideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
            takevideoIntent.setDataAndType(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
            startActivityForResult(takevideoIntent, PICK_VIDEO);
        }
    }
}
