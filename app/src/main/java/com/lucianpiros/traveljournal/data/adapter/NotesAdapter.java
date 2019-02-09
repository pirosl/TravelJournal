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
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.model.Note;
import com.lucianpiros.traveljournal.service.GlideApp;

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
        void onItemSelected(int position);
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
            onItemSelectedListener.onItemSelected(getAdapterPosition());
        }
    }

    /**
     * Class constructor
     *
     * @param notesList notes list managed by this adapte
     * @param onItemSelectedListener selected item listener
     */
    public NotesAdapter(List<Note> notesList, OnItemSelectedListener onItemSelectedListener) {
        this.notesList = notesList;
        this.onItemSelectedListener = onItemSelectedListener;
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
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
}
