package com.lucianpiros.traveljournal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.DataCache;
import com.lucianpiros.traveljournal.model.Adventure;
import com.lucianpiros.traveljournal.model.Note;
import com.lucianpiros.traveljournal.service.DeleteAdventureService;
import com.lucianpiros.traveljournal.service.DeleteNoteService;
import com.lucianpiros.traveljournal.ui.util.UIUtility;
import com.lucianpiros.traveljournal.ui.widget.ConfirmationDialog;
import com.lucianpiros.traveljournal.ui.widget.ProgressBarTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adventure fragment. Displays content generated by all notes within one adventure
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class AdventureFragment extends Fragment implements DeleteAdventureService.DeleteAdventureServiceListener, ConfirmationDialog.ConfirmationDialogActionListener {

    private final static String TAG = AdventureFragment.class.getSimpleName();

    @BindView(R.id.content)
    LinearLayout containerLayout;
    @BindView(R.id.progressbarholder)
    FrameLayout progressBarHolder;
    @BindView(R.id.viewadventure_mainlayout)
    CoordinatorLayout mainLayout;

    private ViewGroup viewGroup;
    private LayoutInflater layoutInflater;
    private ProgressBarTask progressBarTask;
    private Adventure adventure;

    /**
     * Class constructor
     */
    public AdventureFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.viewadventure_menu, menu);

        // change tint color
        UIUtility.changeTintColor(menu, R.id.action_edit, getContext());
        UIUtility.changeTintColor(menu, R.id.action_delete, getContext());
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (UIUtility.getViewPager().getCurrentItem() == 1) {
            menu.findItem(R.id.action_deleteadventure).setVisible(true);
        } else {
            menu.findItem(R.id.action_deleteadventure).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_deleteadventure:
                deleteAdventure();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Delete adventure
     */
    private void deleteAdventure() {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(layoutInflater, getContext());
        confirmationDialog.setConfirmationDialogActionListener(this);
        confirmationDialog.initialize(viewGroup, getString(R.string.deleteadventure_confirmationdialogtitle));
        confirmationDialog.show();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.layoutInflater = inflater;
        this.viewGroup = container;

        View adventureView = inflater.inflate(R.layout.fragment_adventure, container, false);

        ButterKnife.bind(this, adventureView);

        Bundle bundle = getArguments();

        String adventureKey = null;
        if (bundle != null) {
            adventureKey = bundle.getString(getResources()
                    .getString(R.string.noteslistactivity_adventurekey));

            adventure = DataCache.getInstance().getAdventure(adventureKey);

            if (adventure != null && adventure.getNoteKeysList() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(adventure.getAdventureTitle());
                for (String s : adventure.getNoteKeysList()) {
                    TextView valueTV = new TextView(getContext());
                    Note note = DataCache.getInstance().getNote(s);
                    if (note != null) {
                        valueTV.setText(note.getNoteContent());

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        int margin = (int) getResources().getDimension(R.dimen.text_margin);
                        params.setMargins(margin, margin, margin, margin);
                        valueTV.setLayoutParams(params);

                        containerLayout.addView(valueTV);
                    }
                }
            }
        }

        return adventureView;
    }

    @Override
    public void onAccept() {
        progressBarTask = new ProgressBarTask(progressBarHolder, getActivity());
        progressBarTask.execute();

        DeleteAdventureService.getInstance().setDeleteAdventureServiceListener(this);
        DeleteAdventureService.getInstance().deleteAdventure(adventure);
    }

    @Override
    public void onDecline() {
        // nothing to do here
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
                    getActivity().onBackPressed();
                }
            });
        }
    }
}
