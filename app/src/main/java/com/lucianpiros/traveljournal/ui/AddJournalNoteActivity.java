package com.lucianpiros.traveljournal.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
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
import com.lucianpiros.traveljournal.ui.widget.PhotoAlertDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddJournalNoteActivity extends AppCompatActivity implements AddNoteService.AddNoteServiceListener, CustomAlertDialog.CustomDialogActionListener {

    private static final String TAG = AddJournalNoteActivity.class.getSimpleName();

    private static final int PHOTO = 1;
    private static final int VIDEO = 2;
    private static final int PICK_PHOTO = 1;
    private static final int PICK_VIDEO = 2;
    private static final int TAKE_PHOTO = 3;
    private static final int RECORD_VIDEO = 4;



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
    private ProgressBarTask progressBarTask;

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
        } else {
            // set not date and time
            Date noteCreationDate = new Date();

            AddNoteService.getInstance().setNoteCreationDate(noteCreationDate);

            SimpleDateFormat dateSF = new SimpleDateFormat("d MMM yyyy");
            noteDateTV.setText(dateSF.format(noteCreationDate));

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.addnote_menu, menu);

        // change tint color
        Drawable drawable = menu.findItem(R.id.action_save).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorAccent));
        menu.findItem(R.id.action_save).setIcon(drawable);

        return true;
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
            progressBarTask = new ProgressBarTask();
            progressBarTask.execute();

            AddNoteService.getInstance().setContentResolver(getContentResolver());
            AddNoteService.getInstance().setNoteTitle(noteTitleET.getText().toString());
            AddNoteService.getInstance().setNoteContent(noteContentET.getText().toString());

            AddNoteService.getInstance().addNote();
        } else {
            Snackbar snackbar = Snackbar
                    .make(mainLayout, getResources().getString(R.string.notetitleorcontent_empty), Snackbar.LENGTH_SHORT);
            ;
            snackbar.show();
        }
    }

    private boolean isValid(@NotNull TextView textView) {
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
        String alertTitle = getString(R.string.photodoalog_defaulttitle);
        CharSequence uiTitle = noteTitleET.getText();
        if(uiTitle != null && uiTitle.length() > 0) {
            alertTitle = uiTitle.toString();
        }

        photoDialog.initialize(viewGroup, alertTitle, AddNoteService.getInstance().getSelectedPhotoUri());
        photoDialog.showLocal();
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
            Intent takephotoIntent = new Intent(Intent.ACTION_GET_CONTENT,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            takephotoIntent.setType("image/*");
            startActivityForResult(takephotoIntent, PICK_PHOTO);
        }
        if (dialogType == VIDEO) {
            Intent takevideoIntent = new Intent(Intent.ACTION_GET_CONTENT,
                    android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            takevideoIntent.setType("video/*");
            startActivityForResult(takevideoIntent, PICK_VIDEO);
        }
    }

    private class ProgressBarTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarHolder.setVisibility(View.VISIBLE);
            AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBarHolder.setVisibility(View.GONE);
            AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
            AddJournalNoteActivity.this.finish();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                while (true) {
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}