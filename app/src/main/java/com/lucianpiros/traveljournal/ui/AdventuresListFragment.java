package com.lucianpiros.traveljournal.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.FirebaseDB;
import com.lucianpiros.traveljournal.data.adapter.AdapterFilter;
import com.lucianpiros.traveljournal.data.adapter.AdventuresAdapter;
import com.lucianpiros.traveljournal.data.adapter.NotesAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.core.os.ConfigurationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.LinearLayout.VERTICAL;

/**
 * Fragment displaying a list of adventures. Implements @FirebaseDB.AdventuresDBEventsListener so UI updated when underlying data is updated.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class AdventuresListFragment extends Fragment implements FirebaseDB.AdventuresDBEventsListener, AdventuresAdapter.OnItemSelectedListener  {

    public interface OnItemSelectedListener {
        void onItemSelected(String adventureKey);
        void onInitialItemSelected(String adventureKey);
    }

    @BindView(R.id.adventuresrecyclerview)
    RecyclerView adventureRV;

    @BindView(R.id.floatingactionbutton)
    FloatingActionButton fabButton;

    private OnItemSelectedListener onItemSelectedListener;

    /**
     * Class constructor
     */
    public AdventuresListFragment() {

    }

    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_adventureslist, container, false);

        ButterKnife.bind(this, fragmentView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(fragmentView.getContext());
        adventureRV.setLayoutManager(mLayoutManager);
        adventureRV.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration decoration = new DividerItemDecoration(fragmentView.getContext(), VERTICAL);
        adventureRV.addItemDecoration(decoration);

        FirebaseDB.getInstance().setAdventuresDBEventsListener(this);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addAdventureIntent = new Intent(view.getContext(), AddAdventureActivity.class);
                startActivity(addAdventureIntent);
            }
        });
        getActivity().setTitle(getResources().getString(R.string.app_name));

        return fragmentView;
    }

    @Override
    public void OnAdventuresListChanged() {
        AdventuresAdapter adapter = new AdventuresAdapter(this);
        adventureRV.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(String noteKey) {
        onItemSelectedListener.onItemSelected(noteKey);
    }

    @Override
    public void onInitialItemSelected(String noteKey) {
        onItemSelectedListener.onInitialItemSelected(noteKey);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }
}
