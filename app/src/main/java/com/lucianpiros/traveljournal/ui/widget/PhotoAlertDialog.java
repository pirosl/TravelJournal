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

import com.google.firebase.database.annotations.NotNull;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.service.GlideApp;

import java.io.File;

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotoAlertDialog {
    class Title {
        @BindView(R.id.alertdialog_title)
        TextView valueTV;
        @BindView(R.id.alertdialog_title_close)
        ImageView closeBT;

        @OnClick(R.id.alertdialog_title_close)
        protected void closeDialog() {
            alertDialog.dismiss();
        }
    }

    class Body {
        @BindView(R.id.alertdialog_image)
        ImageView imageView;
    }

    private LayoutInflater layoutInflater;
    private Context context;
    private Dialog alertDialog;
    private Uri photoURL;
    private Body dialogBody;

    public PhotoAlertDialog(LayoutInflater layoutInflater, Context context) {
        this.layoutInflater = layoutInflater;
        this.context = context;
    }

    public void initialize(@NotNull ViewGroup viewGroup, String titleRes, Uri photoURL) {
        final Title dialogTitle = new Title();
         dialogBody = new Body();

        this.photoURL = photoURL;

        View titleView = layoutInflater.inflate(R.layout.alertdialog_closabletitle, null);
        ButterKnife.bind(dialogTitle, titleView);
        dialogTitle.valueTV.setText(titleRes);

        View bodyView = layoutInflater.inflate(R.layout.alertdialog_imagebody, viewGroup, false);
        ButterKnife.bind(dialogBody, bodyView);

        AlertDialog.Builder builder = new AlertDialog.Builder(context).setCustomTitle(titleView);

        builder.setView(bodyView);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void showLocal() {
        alertDialog.show();
        dialogBody.imageView.setImageURI(photoURL);
    }
}
