package com.lucianpiros.traveljournal.ui;

import android.os.Bundle;

import com.lucianpiros.traveljournal.R;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity displaying a single note (nested fragment)
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class NoteActivity extends AppCompatActivity {
    private final String LOG_TAG = NoteActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        if (savedInstanceState == null) {
            Bundle arguments = getIntent().getExtras();

            NoteFragment fragment = new NoteFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_note, fragment)
                    .commit();
        }
    }
}