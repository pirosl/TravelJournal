package com.lucianpiros.traveljournal.ui;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lucianpiros.traveljournal.R;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddJournalNoteActivity extends AppCompatActivity {

    @BindView(R.id.fab_addpicture) FloatingActionButton addPictureFAB;
    @BindView(R.id.fab_addmovie) FloatingActionButton addMovieFAB;
    @BindView(R.id.fab_add) FloatingActionButton addFAB;

    private boolean isAddFABExpanded;

    private Animation expandFABAnimation,collapseFABAnimation,closeFABAnimation,openFABAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addjournalnote);

        ButterKnife.bind(this);

        isAddFABExpanded = false;

        expandFABAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_expand);
        collapseFABAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_colapse);
        closeFABAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_close);
        openFABAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_open);
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
}