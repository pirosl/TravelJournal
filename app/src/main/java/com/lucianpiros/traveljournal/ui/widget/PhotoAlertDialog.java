package com.lucianpiros.traveljournal.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.annotations.NotNull;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.service.GlideApp;

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class builds and manages a custom alert dialog used to show a photo.
 * The alert dialog has a transparent title and background. When displayed only
 * the widget showing the photo and a close button are displayed
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class PhotoAlertDialog {
    /**
     * Inner class holding alert dialog body widgets.
     * Used in conjunction with ButterKnife
     *
     * @author Lucian Piros
     * @version 1.0
     */
    class Body {
        @BindView(R.id.alertdialog_image)
        ImageView imageView;

        @BindView(R.id.alertdialog_title_close)
        ImageView closeBT;

        @OnClick(R.id.alertdialog_title_close)
        protected void closeDialog() {
            alertDialog.dismiss();
        }
    }

    // Layout inflater - needed to inflate views within Dialog
    private LayoutInflater layoutInflater;
    private Context context;
    private Dialog alertDialog;
    private Body dialogBody;

    /**
     * Class constructor
     *
     * @param layoutInflater - layout inflater used to inflate Dialog inner views
     * @param context        - Context used within
     */
    public PhotoAlertDialog(LayoutInflater layoutInflater, Context context) {
        this.layoutInflater = layoutInflater;
        this.context = context;
    }

    /**
     * Initialize this alert dialog
     *
     * @param viewGroup view group used when inflating body view
     */
    public void initialize(@NotNull ViewGroup viewGroup) {
        dialogBody = new Body();

        View bodyView = layoutInflater.inflate(R.layout.alertdialog_imagebody, viewGroup, false);
        ButterKnife.bind(dialogBody, bodyView);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(bodyView);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * Display a photo stored locally on device
     *
     * @param photoURI local photo URI
     */
    public void showLocal(Uri photoURI) {
        dialogBody.imageView.setImageURI(photoURI);
        alertDialog.show();
    }

    /**
     * Display a photo stored remotely on Firebase Cloud Storage
     *
     * @param photoURL - file download URL
     */
    public void showRemote(String photoURL) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_errorloaging_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        GlideApp.with(context).load(photoURL).apply(options).into(dialogBody.imageView);
        alertDialog.show();
    }
}
