package com.lucianpiros.traveljournal.ui.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.DataCache;
import com.lucianpiros.traveljournal.model.Note;
import com.lucianpiros.traveljournal.service.GlideApp;
import com.lucianpiros.traveljournal.ui.MainActivity;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class AppNotesWidget extends AppWidgetProvider {

    private static AppWidgetTarget appWidgetTarget;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, Note note,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_notes_widget);

        views.setTextViewText(R.id.notetitle, note.getNoteTitle());
        views.setTextViewText(R.id.notecontent, note.getNoteContent());

        String recipePhotoURL = note.getPhotoDownloadURL();
        if (recipePhotoURL != null) {

            appWidgetTarget = new AppWidgetTarget(context, R.id.noteimage, views, appWidgetId) {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    super.onResourceReady(resource, transition);
                }
            };

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_errorloaging_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            GlideApp.with(context).asBitmap().load(recipePhotoURL).apply(options).into(appWidgetTarget);
        }

        // Create an Intent to launch MainActivity when clicked
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);

        views.setOnClickPendingIntent(R.id.app_widget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        List<Note> notes = DataCache.getInstance().getNotesList();

        // if notes list is empty return
        if (notes == null || notes.size() == 0) {
            return;
        }

        Note note = notes.get(notes.size() - 1);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_notes_widget);

        views.setTextViewText(R.id.notetitle, note.getNoteTitle());
        views.setTextViewText(R.id.notecontent, note.getNoteContent());

        String recipePhotoURL = note.getPhotoDownloadURL();
        if (recipePhotoURL != null) {

            appWidgetTarget = new AppWidgetTarget(context, R.id.noteimage, views, appWidgetIds) {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    super.onResourceReady(resource, transition);
                }
            };

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_errorloaging_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            GlideApp.with(context).asBitmap().load(recipePhotoURL).apply(options).into(appWidgetTarget);
        }

        // Create an Intent to launch MainActivity when clicked
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);

        views.setOnClickPendingIntent(R.id.app_widget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    /**
     * Updates all widget instances given the widget Ids and display information
     *
     * @param context          The calling context
     * @param appWidgetManager The widget manager
     * @param note             Note
     * @param appWidgetIds     Array of widget Ids to be updated
     */
    public static void updateNoteWidgets(Context context, AppWidgetManager appWidgetManager,
                                         Note note, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, note, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
