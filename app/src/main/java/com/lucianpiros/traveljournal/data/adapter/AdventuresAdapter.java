package com.lucianpiros.traveljournal.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.DataCache;
import com.lucianpiros.traveljournal.model.Adventure;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AdventuresAdapter extends RecyclerView.Adapter<AdventuresAdapter.ViewHolder> {

    private final static String TAG = AdventuresAdapter.class.getSimpleName();

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
    private List<Adventure> adventuresList;

    // OnItemSelectedListene
    private OnItemSelectedListener onItemSelectedListener;

    /**
     * ViewHolder implementation
     *
     * @author Lucian Piros
     * @version 1.0
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.adventuretitle)
        TextView adventureTitleTV;
        @BindView(R.id.adventurecontent) TextView adventureContentTV;
        @BindView(R.id.adventure_rootlayout)
        FrameLayout layout;

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Adventure adventure = adventuresList.get(getAdapterPosition());
            onItemSelectedListener.onItemSelected(adventure.getAdventureKey());
        }
    }

    /**
     * Class constructor
     *
     * @param onItemSelectedListener selected item listener
     */
    public AdventuresAdapter(OnItemSelectedListener onItemSelectedListener) {
        this.adventuresList = DataCache.getInstance().getAdventuresList();
        this.onItemSelectedListener = onItemSelectedListener;

        if(this.adventuresList.size() > 0) {
            this.onItemSelectedListener.onInitialItemSelected(this.adventuresList.get(0).getAdventureKey());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adventurerow, parent, false);

        return new AdventuresAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Adventure adventure = adventuresList.get(position);
        // Populate the data into the template view using the data object
        holder.adventureTitleTV.setText(adventure.getAdventureTitle());
        holder.adventureContentTV.setText(adventure.getAdventureDescription());
    }

    @Override
    public int getItemCount() {
        return adventuresList.size();
    }
}
