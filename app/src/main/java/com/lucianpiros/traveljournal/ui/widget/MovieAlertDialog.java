package com.lucianpiros.traveljournal.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.database.annotations.NotNull;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.FirebaseCS;
import com.lucianpiros.traveljournal.service.GlideApp;

import java.io.File;

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class builds and manages a custom alert dialog used to show a video.
 * The alert dialog has a transparent title and background. When displayed only
 * the widget showing the movie and a close button are displayed
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class MovieAlertDialog implements FirebaseCS.FileDownloadListener {
    private static final String TMP_LOCALFILE = "tmp_movie.mp4";

    /**
     * Inner class holding alert dialog title widgets.
     * Used in conjunction with ButterKnife
     *
     * @author Lucian Piros
     * @version 1.0
     */
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
    public MovieAlertDialog(LayoutInflater layoutInflater, Context context) {
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

        View bodyView = layoutInflater.inflate(R.layout.alertdialog_moviebody, viewGroup, false);
        ButterKnife.bind(dialogBody, bodyView);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(bodyView);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * Play a movie stored locally on device
     *
     * @param movieUri - local movie URI
     */
    public void showLocal(Uri movieUri) {
        initializePlayerWithLocalSource(movieUri);
        alertDialog.show();
    }

    /**
     * Initialize exo player to play provided URI
     *
     * @param uri - file's URI to be played
     */
    private void initializePlayerWithLocalSource(Uri uri) {
        MediaSource mediaSource = new ExtractorMediaSource(uri,
                new DefaultDataSourceFactory(context, "ua"),
                new DefaultExtractorsFactory(), null, null);

        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context);
        player.prepare(mediaSource);

        dialogBody.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

        dialogBody.playerView.setPlayer(player);
    }

    /**
     * Play a movie stored remotely on Firebase cloud storage.
     * First the movie is downloaded locally then played from local device storage
     *
     * @param noteKey   - FirebaseBD note key. Used to retrieve note associated movie
     * @param movieName - movie file name
     */
    public void showRemote(String noteKey, String movieName) {
        FirebaseCS.getInstance().setFileDownloadListener(this);
        FirebaseCS.getInstance().dowloadFile(noteKey, movieName, TMP_LOCALFILE);
        alertDialog.show();
    }

    @Override
    public void onComplete(boolean success) {
        if (success) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), TMP_LOCALFILE);
            initializePlayerWithLocalSource(Uri.parse(file.getPath()));
        }
    }
}
