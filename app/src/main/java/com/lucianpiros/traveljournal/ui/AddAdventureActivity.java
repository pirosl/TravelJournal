package com.lucianpiros.traveljournal.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.adapter.SelectableNotesAdapter;
import com.lucianpiros.traveljournal.model.Adventure;
import com.lucianpiros.traveljournal.service.AddAdventureService;
import com.lucianpiros.traveljournal.ui.util.UIUtility;
import com.lucianpiros.traveljournal.ui.widget.ProgressBarTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.LinearLayout.VERTICAL;

/**
 * Add adventure activity.
 * Used for adding journal adventures
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class AddAdventureActivity extends AppCompatActivity implements AddAdventureService.AddAdventureServiceListener {
    private static final String TAG = AddAdventureActivity.class.getSimpleName();

    @BindView(R.id.adventure_title)
    EditText adventureTitleET;
    @BindView(R.id.adventure_description)
    EditText adventureDescriptionET;
    @BindView(R.id.addadventure_mainlayout)
    CoordinatorLayout mainLayout;
    @BindView(R.id.progressbarholder)
    FrameLayout progressBarHolder;
    @BindView(R.id.selectablenotesrecyclerview)
    RecyclerView selectableNotesRV;

    private SelectableNotesAdapter adapter;
    // ProgressBar to be displayed when add / save operation takes place.
    private ProgressBarTask progressBarTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addadventure);

        ButterKnife.bind(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        selectableNotesRV.setLayoutManager(mLayoutManager);
        selectableNotesRV.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        selectableNotesRV.addItemDecoration(decoration);

        adapter = new SelectableNotesAdapter();
        selectableNotesRV.setAdapter(adapter);

        getSupportActionBar().setTitle(getString(R.string.addadventure_title));
        AddAdventureService.getInstance().setAddAdventureServiceListener(this);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_save:
                addAdventure();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Add adventure to Firebase
     */
    private void addAdventure() {
        if (UIUtility.isValid(adventureTitleET)) {
            progressBarTask = new ProgressBarTask(progressBarHolder, this);
            progressBarTask.execute();

            Adventure adventure = new Adventure();
            adventure.setAdventureTitle(adventureTitleET.getText().toString());
            if (adventureDescriptionET.getText() != null) {
                adventure.setAdventureDescription(adventureDescriptionET.getText().toString());
            }
            adventure.setNoteKeysList(adapter.getSelectedNotes());

            AddAdventureService.getInstance().addAdventure(adventure);
        } else {
            Snackbar snackbar = Snackbar
                    .make(mainLayout, getResources().getString(R.string.adventuretitle_empty), Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    @Override
    public void onComplete() {
        progressBarTask.cancel(true);
        Snackbar snackbar = Snackbar
                .make(mainLayout, getResources().getString(R.string.addadventure_success), Snackbar.LENGTH_SHORT);

        snackbar.show();

        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                finish();
            }
        });
    }
}
