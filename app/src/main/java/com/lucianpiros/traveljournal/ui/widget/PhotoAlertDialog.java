package com.lucianpiros.traveljournal.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.annotations.NotNull;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.service.GlideApp;

import java.io.File;

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotoAlertDialog {
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

    private LayoutInflater layoutInflater;
    private Context context;
    private Dialog alertDialog;
    private Body dialogBody;

    public PhotoAlertDialog(LayoutInflater layoutInflater, Context context) {
        this.layoutInflater = layoutInflater;
        this.context = context;
    }

    public void initialize(@NotNull ViewGroup viewGroup) {
        dialogBody = new Body();

        View bodyView = layoutInflater.inflate(R.layout.alertdialog_imagebody, viewGroup, false);
        ButterKnife.bind(dialogBody, bodyView);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(bodyView);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void showLocal( Uri photoURI) {
        dialogBody.imageView.setImageURI(photoURI);
        alertDialog.show();
    }

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
