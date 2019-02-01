package com.lucianpiros.traveljournal.ui;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.annotations.NotNull;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.FirebaseDB;
import com.lucianpiros.traveljournal.model.Note;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddJournalNoteActivity extends AppCompatActivity implements FirebaseDB.OnDBCompleteListener {

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
    @BindView(R.id.note_picture)
    ImageView notePictureIV;
    @BindView(R.id.note_content)
    EditText noteContentET;

    class CustomAlertDialog {
        public static final int TAKE_PHOTO = 1;
        public static final int TAKE_VIDEO = 2;

        private int dialogType;

        public CustomAlertDialog(int dialogType) {
            this.dialogType = dialogType;
        }

        class Title {
            @BindView(R.id.alertdialog_title)
            TextView valueTV;
        }

        class Body {
            @BindView(R.id.button_option1)
            Button option1BT;
            @BindView(R.id.button_option2)
            Button option2BT;
            @BindView(R.id.button_option3)
            Button option3BT;

            @OnClick(R.id.button_option2)
            protected void take_photo_video() {
                alertDialog.dismiss();
                AddJournalNoteActivity.this.launchIntent();
            }

            @OnClick(R.id.button_option3)
            protected void closeAlertDialog() {
                alertDialog.dismiss();
            }
        }
    }

    private Date noteCreationDate;
    private boolean isAddFABExpanded;

    private Dialog alertDialog;

    private Animation expandFABAnimation, collapseFABAnimation, closeFABAnimation, openFABAnimation;

    protected void launchIntent() {
        //if(dialogType == TAKE_PHOTO) {
        Intent takephotoIntent = new Intent(Intent.ACTION_GET_CONTENT,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(takephotoIntent, CustomAlertDialog.TAKE_PHOTO);
        //}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addjournalnote);

        ButterKnife.bind(this);

        FirebaseDB.getInstance().setOnDBCompleteListener(this);

        isAddFABExpanded = false;
        expandFABAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_expand);
        collapseFABAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_colapse);
        closeFABAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_close);
        openFABAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_open);

        // set not date and time
        noteCreationDate = new Date();
        SimpleDateFormat dateSF = new SimpleDateFormat("d MMM yyyy");
        noteDateTV.setText(dateSF.format(noteCreationDate));
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
            Note note = new Note();
            note.setNoteTitle(noteTitleET.getText().toString());
            note.setNoteContent(noteContentET.getText().toString());
            note.setNoteCreationDate(noteCreationDate);

            FirebaseDB.getInstance().save(note);
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

        CustomAlertDialog customAlertDialog = new CustomAlertDialog(CustomAlertDialog.TAKE_PHOTO);
        final CustomAlertDialog.Title alertDialogTitle = customAlertDialog.new Title();
        final CustomAlertDialog.Body alertDialogBody = customAlertDialog.new Body();

        LayoutInflater inflater = this.getLayoutInflater();
        ViewGroup viewGroup = findViewById(android.R.id.content);

        View titleView = inflater.inflate(R.layout.alertdialolg_title, null);
        ButterKnife.bind(alertDialogTitle, titleView);
        alertDialogTitle.valueTV.setText(R.string.add_photo);

        View bodyView = inflater.inflate(R.layout.alertdialog_body, viewGroup, false);
        ButterKnife.bind(alertDialogBody, bodyView);
        alertDialogBody.option1BT.setText(R.string.add_photo_option1);
        alertDialogBody.option2BT.setText(R.string.add_photo_option2);
        alertDialogBody.option3BT.setText(R.string.add_photo_option3);

        AlertDialog.Builder builder = new AlertDialog.Builder(AddJournalNoteActivity.this).setCustomTitle(titleView);

        builder.setView(bodyView);
        alertDialog = builder.create();

        alertDialog.show();
    }

    @OnClick(R.id.fab_addmovie)
    protected void addMovie() {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(CustomAlertDialog.TAKE_VIDEO);
        final CustomAlertDialog.Title alertDialogTitle = customAlertDialog.new Title();
        final CustomAlertDialog.Body alertDialogBody = customAlertDialog.new Body();

        LayoutInflater inflater = this.getLayoutInflater();
        ViewGroup viewGroup = findViewById(android.R.id.content);

        View titleView = inflater.inflate(R.layout.alertdialolg_title, null);
        ButterKnife.bind(alertDialogTitle, titleView);
        alertDialogTitle.valueTV.setText(R.string.add_video);

        View bodyView = inflater.inflate(R.layout.alertdialog_body, viewGroup, false);
        ButterKnife.bind(alertDialogBody, bodyView);
        alertDialogBody.option1BT.setText(R.string.add_video_option1);
        alertDialogBody.option2BT.setText(R.string.add_video_option2);
        alertDialogBody.option3BT.setText(R.string.add_video_option3);

        AlertDialog.Builder builder = new AlertDialog.Builder(AddJournalNoteActivity.this).setCustomTitle(titleView);

        builder.setView(bodyView);
        alertDialog = builder.create();

        alertDialog.show();
    }


    @Override
    public void onComplete(boolean success) {
        Snackbar snackbar = Snackbar
                .make(mainLayout, getResources().getString(R.string.addnote_success), Snackbar.LENGTH_SHORT);
        ;
        if (!success) {
            snackbar.setText(getResources().getString(R.string.addnote_error));
        }

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

        if (requestCode == CustomAlertDialog.TAKE_PHOTO && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                notePictureIV.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}