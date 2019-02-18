package com.lucianpiros.traveljournal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.DataCache;
import com.lucianpiros.traveljournal.model.Adventure;
import com.lucianpiros.traveljournal.model.Note;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adventure fragment. Displays content generated by all notes within one adventure
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class AdventureFragment extends Fragment {

    private final static String TAG = AdventureFragment.class.getSimpleName();

    @BindView(R.id.content)
    LinearLayout containerLayout;

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

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View adventureView = inflater.inflate(R.layout.fragment_adventure, container, false);

        ButterKnife.bind(this, adventureView);

        Bundle bundle = getArguments();

        String adventureKey = null;
        if (bundle != null) {
            adventureKey = bundle.getString(getResources()
                    .getString(R.string.noteslistactivity_adventurekey));

            Adventure adventure = DataCache.getInstance().getAdventure(adventureKey);

            if (adventure != null) {
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
}
