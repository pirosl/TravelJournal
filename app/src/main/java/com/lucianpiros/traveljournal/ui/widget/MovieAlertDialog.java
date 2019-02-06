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

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.firebase.database.annotations.NotNull;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.service.GlideApp;

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieAlertDialog {
    class Body {
        @BindView(R.id.alertdialog_player)
        PlayerView playerView;

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
    private Uri movieUri;
    private Body dialogBody;

    public MovieAlertDialog(LayoutInflater layoutInflater, Context context) {
        this.layoutInflater = layoutInflater;
        this.context = context;
    }

    public void initialize(@NotNull ViewGroup viewGroup, Uri moviewUri) {
        dialogBody = new Body();

        this.movieUri = moviewUri;

        View bodyView = layoutInflater.inflate(R.layout.alertdialog_moviebody, viewGroup, false);
        ButterKnife.bind(dialogBody, bodyView);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(bodyView);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void showLocal() {
        initializePlayer(movieUri);
        alertDialog.show();
    }

    private void initializePlayer(Uri uri) {
        MediaSource mediaSource = new ExtractorMediaSource(uri,
                new DefaultDataSourceFactory(context,"ua"),
                new DefaultExtractorsFactory(), null, null);

        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context);
        player.prepare(mediaSource);

        dialogBody.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

        dialogBody.playerView.setPlayer(player);
    }
}
