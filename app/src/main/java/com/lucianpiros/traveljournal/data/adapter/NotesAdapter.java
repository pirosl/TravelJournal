package com.lucianpiros.traveljournal.data.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.model.Note;
import com.lucianpiros.traveljournal.service.GlideApp;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    // Recipies list
    private List<Note> notesList;

    // OnItemSelectedListene
    private OnItemSelectedListener onItemSelectedListener;

    /**
     * ViewHolder implementation
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
     * @param notesList
     */
    public NotesAdapter(List<Note> notesList, OnItemSelectedListener onItemSelectedListener) {
        this.notesList = notesList;
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.journalnoterow, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Note recipe = notesList.get(position);
        // Populate the data into the template view using the data object
        holder.noteTitleTV.setText(recipe.getNoteTitle());
        holder.noteContentTV.setText(recipe.getNoteContent());

        String recipePhotoURL = recipe.getPhotoDownloadURL();
        if(recipePhotoURL != null) {
            Log.d("Test", "image at " + position);
            GlideApp.with(holder.itemView.getContext()).load(recipePhotoURL).into(holder.noteImageIV);
        }
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
}
