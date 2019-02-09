package com.lucianpiros.traveljournal.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.annotations.NotNull;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.service.AddNoteService;
import com.lucianpiros.traveljournal.ui.widget.CustomAlertDialog;
import com.lucianpiros.traveljournal.ui.widget.MovieAlertDialog;
import com.lucianpiros.traveljournal.ui.widget.PhotoAlertDialog;
import com.lucianpiros.traveljournal.ui.widget.ProgressBarTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.os.ConfigurationCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class EditableJournalNoteActivity extends AppCompatActivity implements CustomAlertDialog.CustomDialogActionListener {

    private static final String TAG = EditableJournalNoteActivity.class.getSimpleName();

    protected static final int PHOTO = 1;
    protected static final int VIDEO = 2;
    protected static final int PICK_PHOTO = 1;
    protected static final int PICK_VIDEO = 2;
    protected static final int TAKE_PHOTO = 3;

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
    private Animation expandFABAnimation, collapseFABAnimation, closeFABAnimation, openFABAnimation;
    protected ProgressBarTask progressBarTask;

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

    public void onOption1(int dialogType) {
    /*    if (dialogType == PHOTO) {
            Intent takephotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takephotoIntent.resolveActivity(getPackageManager()) != null){
                String timeStamp =
                        new SimpleDateFormat("yyyyMMdd_HHmmss",
                                Locale.getDefault()).format(new Date());
                String file = "IMG_" + timeStamp +".jpg";
                File newfile = new File(file);
                try {
                    newfile.createNewFile();
                }
                catch (IOException e)
                {
                }

                //     Uri outputFileUri = Uri.fromFile(newfile);
                Uri outputFileUri = FileProvider.getUriForFile(AddJournalNoteActivity.this, BuildConfig.APPLICATION_ID, newfile);

                    takephotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(takephotoIntent, TAKE_PHOTO);
            }
        }*/
    }

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
