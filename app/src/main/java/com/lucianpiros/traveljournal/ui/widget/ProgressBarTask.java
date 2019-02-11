package com.lucianpiros.traveljournal.ui.widget;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import java.util.concurrent.TimeUnit;

/**
 * Class managing / showing a progress bar.
 * Used in persistent operations.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class ProgressBarTask extends AsyncTask<Void, Void, Void> {

    private ViewGroup progressBarHolder;
    private Activity activity;

    public ProgressBarTask(ViewGroup progressBarHolder, Activity activity) {
        this.progressBarHolder = progressBarHolder;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBarHolder.setVisibility(View.VISIBLE);
        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressBarHolder.setVisibility(View.GONE);
        AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
        activity.finish();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            while (true) {
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}