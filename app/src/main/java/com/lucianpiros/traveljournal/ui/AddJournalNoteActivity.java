package com.lucianpiros.traveljournal.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.FirebaseDB;
import com.lucianpiros.traveljournal.model.Note;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddJournalNoteActivity extends AppCompatActivity implements FirebaseDB.OnDBCompleteListener {

    @BindView(R.id.addnote_mainlayout) CoordinatorLayout mainLayout;
    @BindView(R.id.fab_addpicture) FloatingActionButton addPictureFAB;
    @BindView(R.id.fab_addmovie) FloatingActionButton addMovieFAB;
    @BindView(R.id.fab_add) FloatingActionButton addFAB;
    @BindView(R.id.note_title) EditText noteTitleET;
    @BindView(R.id.note_date) TextView noteDateTV;
    @BindView(R.id.note_content) EditText noteContentET;

    private Date noteCreationDate;

    private boolean isAddFABExpanded;

    private Animation expandFABAnimation,collapseFABAnimation,closeFABAnimation,openFABAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addjournalnote);

        ButterKnife.bind(this);

        FirebaseDB.getInstance().setOnDBCompleteListener(this);

        isAddFABExpanded = false;
        expandFABAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_expand);
        collapseFABAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_colapse);
        closeFABAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_close);
        openFABAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_open);

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
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.colorAccent));
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
        Note note = new Note();
        note.setNoteTitle(noteTitleET.getText().toString());
        note.setNoteContent(noteContentET.getText().toString());
        note.setNoteCreationDate(noteCreationDate);

        // add note to Firebase database
        FirebaseDB.getInstance().save(note);
    }

    @OnClick(R.id.fab_add)
    public void annimateAddFAB() {
        if(isAddFABExpanded) {
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

    @Override
    public void onCoplete(boolean success) {
        Snackbar snackbar = Snackbar
                .make(mainLayout, getResources().getString(R.string.addnote_success), Snackbar.LENGTH_SHORT);;
        if(!success) {
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
}