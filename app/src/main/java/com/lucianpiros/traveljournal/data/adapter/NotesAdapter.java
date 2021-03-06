package com.lucianpiros.traveljournal.data.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.DataCache;
import com.lucianpiros.traveljournal.model.Note;
import com.lucianpiros.traveljournal.service.GlideApp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * RecyclerView.Adapter implementation for journal notes recycler view.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private final static String TAG = NotesAdapter.class.getSimpleName();

    /**
     * Method of this interface are called when item is selected / tapped on.
     *
     * @author Lucian Piros
     * @version 1.0
     */
    public interface OnItemSelectedListener {
        void onItemSelected(String noteKey);
        void onInitialItemSelected(String noteKey);
    }

    // Recipies list
    private List<Note> notesList;

    // OnItemSelectedListene
    private OnItemSelectedListener onItemSelectedListener;

    /**
     * ViewHolder implementation
     *
     * @author Lucian Piros
     * @version 1.0
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.notetitle) TextView noteTitleTV;
        @BindView(R.id.notecontent) TextView noteContentTV;
        @BindView(R.id.noteimage) ImageView noteImageIV;
        @BindView(R.id.note_rootlayout) FrameLayout layout;

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Note note = notesList.get(getAdapterPosition());
            onItemSelectedListener.onItemSelected(note.getNoteKey());
        }
    }

    /**
     * Class constructor
     *
     * @param onItemSelectedListener selected item listener
     */
    public NotesAdapter(OnItemSelectedListener onItemSelectedListener, AdapterFilter adapterFilter) {
        if(!adapterFilter.isFiltered()) {
            this.notesList = DataCache.getInstance().getNotesList();
        }
        else {
            if(adapterFilter.getFilterType() == AdapterFilter.FILTERTYPE_DATE) {
                Calendar calendar = adapterFilter.getCalendar();
                this.notesList = new ArrayList<>();
                List<Note> workingList = DataCache.getInstance().getNotesList();
                for(Note n : workingList) {
                    Calendar cn = Calendar.getInstance();
                    cn.setTime(n.getNoteCreationDate());
                    if(calendar.get(Calendar.YEAR) == cn.get(Calendar.YEAR) &&
                            calendar.get(Calendar.MONTH) == cn.get(Calendar.MONTH) &&
                            calendar.get(Calendar.DAY_OF_MONTH) == cn.get(Calendar.DAY_OF_MONTH)) {
                        this.notesList.add(n);
                    }
                }
            }

            if(adapterFilter.getFilterType() == AdapterFilter.FILTERTYPE_GEOFENCE) {
                LatLng latLng = adapterFilter.getLatLng();
                this.notesList = new ArrayList<>();
                List<Note> workingList = DataCache.getInstance().getNotesList();
                for(Note n : workingList) {
                    LatLng noteLatLng = new LatLng(n.getLatitude(), n.getLongitude());
                    double distance = SphericalUtil.computeDistanceBetween(noteLatLng, latLng);
                    if(distance < 1000) {
                        this.notesList.add(n);
                    }
                }
            }
        }
        this.onItemSelectedListener = onItemSelectedListener;

        if(this.notesList.size() > 0) {
            this.onItemSelectedListener.onInitialItemSelected(this.notesList.get(0).getNoteKey());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.journalnoterow, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note recipe = notesList.get(position);
        // Populate the data into the template view using the data object
        holder.noteTitleTV.setText(recipe.getNoteTitle());
        holder.noteContentTV.setText(recipe.getNoteContent());

        String recipePhotoURL = recipe.getPhotoDownloadURL();
        if(recipePhotoURL != null) {
            Log.d(TAG, "Loading image at " + recipePhotoURL);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_errorloaging_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            GlideApp.with(holder.itemView.getContext()).load(recipePhotoURL).apply(options).into(holder.noteImageIV);
        }
        else {
            holder.noteImageIV.setImageResource(android.R.color.transparent);
        }
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
}
