package com.lucianpiros.traveljournal.data.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.DataCache;
import com.lucianpiros.traveljournal.model.Note;
import com.lucianpiros.traveljournal.service.GlideApp;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * RecyclerView.Adapter implementation for selectable notes recycler view.
 * Will be used within Adventure list to vallow to select which notes belong to an adventure
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class SelectableNotesAdapter extends RecyclerView.Adapter<SelectableNotesAdapter.ViewHolder> {

    private final static String TAG = SelectableNotesAdapter.class.getSimpleName();

    // Recipies list
    private List<Note> notesList;
    private List<Boolean> selectedValueList;

    /**
     * ViewHolder implementation
     *
     * @author Lucian Piros
     * @version 1.0
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.notetitle)
        TextView noteTitleTV;
        @BindView(R.id.notecontent) TextView noteContentTV;
        @BindView(R.id.noteimage)
        ImageView noteImageIV;
        @BindView(R.id.notecheck)
        CheckBox noteCheckCB;

        @BindView(R.id.note_rootlayout)
        FrameLayout layout;

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            layout.setOnClickListener(this);

            noteCheckCB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    selectedValueList.set(position, !selectedValueList.get(position));
                }
            });
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            selectedValueList.set(position, !selectedValueList.get(position));
            noteCheckCB.setChecked(!noteCheckCB.isChecked());
        }
    }

    /**
     * Class constructor
     *
     */
    public SelectableNotesAdapter() {
        this.notesList = DataCache.getInstance().getNotesList();
        this.selectedValueList = new ArrayList<>();
        for(int i = 0; i < this.notesList.size(); i++) {
            this.selectedValueList.add(false);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selectablejournalnoterow, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note recipe = notesList.get(position);
        // Populate the data into the template view using the data object
        holder.noteTitleTV.setText(recipe.getNoteTitle());
        holder.noteContentTV.setText(recipe.getNoteContent());
        holder.noteCheckCB.setChecked(this.selectedValueList.get(position));

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
        if(notesList == null)
            return 0;
        return notesList.size();
    }

    /**
     * Returns a list of selected notes (notekey)
     *
     * @return Returns a list of selected notes (notekey)
     */
    public List<String> getSelectedNotes() {
        List<String> selectedNotes = new ArrayList<>();
        for(int i = 0; i < notesList.size(); i++) {
            if(selectedValueList.get(i)) {
                selectedNotes.add(notesList.get(i).getNoteKey());
            }
        }

        return selectedNotes;
    }
}
