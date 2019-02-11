package com.lucianpiros.traveljournal.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.annotations.NotNull;
import com.lucianpiros.traveljournal.R;

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class builds and manages a custom alert dialog used when selecting photo or videos.
 * Alert dialog title has a custom color.
 * Alert dialog body contains 3 buttons with custom background and tint color. Each button
 * displays an option on where phot/video can be selected from.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class CustomAlertDialog {

    /**
     * Methids from this interface are called when buttons are tapped / pressed
     *
     * @author Lucian Piros
     * @version 1.0
     */
    public interface CustomDialogActionListener {
        void onOption1(int dialogType);

        void onOption2(int dialogType);
    }

    /**
     * Inner class holding alert dialog title widgets.
     * Used in conjunction with ButterKnife
     *
     * @author Lucian Piros
     * @version 1.0
     */
    class Title {
        @BindView(R.id.alertdialog_title)
        TextView valueTV;
    }

    /**
     * Inner class holding alert dialog body widgets.
     * Used in conjunction with ButterKnife
     *
     * @author Lucian Piros
     * @version 1.0
     */
    class Body {
        @BindView(R.id.button_option1)
        Button option1BT;
        @BindView(R.id.button_option2)
        Button option2BT;
        @BindView(R.id.button_option3)
        Button option3BT;

        @OnClick(R.id.button_option1)
        protected void onOption1() {
            alertDialog.dismiss();
            customDialogActionListener.onOption1(dialogType);
        }

        @OnClick(R.id.button_option2)
        protected void onOption2() {
            alertDialog.dismiss();
            customDialogActionListener.onOption2(dialogType);
        }

        @OnClick(R.id.button_option3)
        protected void onOption3() {
            alertDialog.dismiss();
        }
    }

    // Dialog type. Can be PHOTO or MOVIE
    private int dialogType;
    // Layout inflater - needed to inflate views within Dialog
    private LayoutInflater layoutInflater;
    private Context context;
    private Dialog alertDialog;
    private CustomDialogActionListener customDialogActionListener;

    /**
     * @param dialogType     - dialog type (eg. PHOTO or MOVIE)
     * @param layoutInflater - layout inflater used to inflate Dialog inner views
     * @param context        - Context used within
     */
    public CustomAlertDialog(int dialogType, LayoutInflater layoutInflater, Context context) {
        this.dialogType = dialogType;
        this.layoutInflater = layoutInflater;
        this.context = context;
    }

    /**
     * Initialise this alert dialog
     *
     * @param viewGroup    view group used when inflating body view
     * @param titleResid   String used as title for this alert dialog
     * @param option1Resid String used for option1 button
     * @param option2Resid String used for option1 button
     * @param option3Resid String used for option1 button
     */
    public void initialize(@NotNull ViewGroup viewGroup, int titleResid, int option1Resid, int option2Resid, int option3Resid) {
        final CustomAlertDialog.Title alertDialogTitle = new Title();
        final CustomAlertDialog.Body alertDialogBody = new Body();

        View titleView = layoutInflater.inflate(R.layout.alertdialolg_title, null);
        ButterKnife.bind(alertDialogTitle, titleView);
        alertDialogTitle.valueTV.setText(titleResid);

        View bodyView = layoutInflater.inflate(R.layout.alertdialog_body, viewGroup, false);
        ButterKnife.bind(alertDialogBody, bodyView);
        alertDialogBody.option1BT.setText(option1Resid);
        alertDialogBody.option2BT.setText(option2Resid);
        alertDialogBody.option3BT.setText(option3Resid);

        AlertDialog.Builder builder = new AlertDialog.Builder(context).setCustomTitle(titleView);

        builder.setView(bodyView);
        alertDialog = builder.create();
    }

    public void setCustomDialogActionListener(CustomDialogActionListener customDialogActionListener) {
        this.customDialogActionListener = customDialogActionListener;
    }

    /**
     * Show custom built alert dialog
     */
    public void show() {
        alertDialog.show();
    }
}