package com.lucianpiros.traveljournal.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.os.ConfigurationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.FirebaseDB;
import com.lucianpiros.traveljournal.data.adapter.AdapterFilter;
import com.lucianpiros.traveljournal.data.adapter.NotesAdapter;
import com.lucianpiros.traveljournal.model.Note;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static android.widget.LinearLayout.VERTICAL;

/**
 * Fragment displaying a list of notes. Implements @FirebaseDB.NoteDBEventsListener so UI updated when underlying data is updated.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class JournalNotesListFragment extends Fragment implements FirebaseDB.NoteDBEventsListener, NotesAdapter.OnItemSelectedListener {

    public interface OnItemSelectedListener {
        void onItemSelected(String noteKey);
    }

    @BindView(R.id.notesrecyclerview) RecyclerView noteRV;

    @BindView(R.id.floatingactionbutton) FloatingActionButton fabButton;

    private OnItemSelectedListener onItemSelectedListener;

    private AdapterFilter adapterFilter;

    /**
     * Class constructor
     */
    public JournalNotesListFragment() {

    }

    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_journalnoteslist, container, false);

        ButterKnife.bind(this, fragmentView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(fragmentView.getContext());
        noteRV.setLayoutManager(mLayoutManager);
        noteRV.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration decoration = new DividerItemDecoration(fragmentView.getContext(), VERTICAL);
        noteRV.addItemDecoration(decoration);

        FirebaseDB.getInstance().setNoteDBEventsListener(this);

        adapterFilter = new AdapterFilter();
        Bundle arguments = getArguments();
        if(arguments != null) {
            adapterFilter.setFiltered(arguments.getBoolean(getResources().getString(R.string.noteslistactivity_filtered)));
            adapterFilter.setFilterType(arguments.getInt(getResources().getString(R.string.noteslistactivity_type)));
            if(adapterFilter.getFilterType() == AdapterFilter.FILTERTYPE_DATE) {
                long millis = arguments.getLong(getResources().getString(R.string.noteslistactivity_calendar));
                String timezone = arguments.getString(getResources().getString(R.string.noteslistactivity_calendar_timezone));

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone(timezone));
                calendar.setTimeInMillis(millis);
                adapterFilter.setCalendar(calendar);

                SimpleDateFormat dateSF = new SimpleDateFormat(getString(R.string.note_dateformat), ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0));
                String message = String.format(getResources().getString(R.string.noteslistfiltered_ondate), dateSF.format(calendar.getTime()));
                getActivity().setTitle(message);
            }
        }

        if(!adapterFilter.isFiltered()) {
            fabButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addNoteIntent = new Intent(view.getContext(), AddJournalNoteActivity.class);
                    startActivity(addNoteIntent);
                }
            });
        }
        else {
            fabButton.hide();
        }
        return fragmentView;
    }

    @Override
    public void OnNotesListChanged() {
        NotesAdapter adapter = new NotesAdapter(this, adapterFilter);
        noteRV.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(String noteKey) {
        onItemSelectedListener.onItemSelected(noteKey);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }
}
