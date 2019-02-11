package com.lucianpiros.traveljournal.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.annotations.NotNull;
import com.lucianpiros.traveljournal.R;

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class builds and manages a custom alert dialog.
 * Alert dialog title has a custom color.
 * Alert dialog body contains 2 buttons with custom background and tint color
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class ConfirmationDialog {

    /**
     * Methids from this interface are called when buttons are tapped / pressed
     *
     * @author Lucian Piros
     * @version 1.0
     */
    public interface ConfirmationDialogActionListener {
        void onAccept();

        void onDecline();
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
        @BindView(R.id.button_accept)
        ImageButton option1BT;
        @BindView(R.id.button_decline)
        ImageButton option2BT;

        @OnClick(R.id.button_accept)
        protected void onAccept() {
            alertDialog.dismiss();
            confirmationDialogActionListener.onAccept();
        }

        @OnClick(R.id.button_decline)
        protected void onDecline() {
            alertDialog.dismiss();
            confirmationDialogActionListener.onDecline();
        }
    }

    // ConfirnationDialogAction listener
    private ConfirmationDialogActionListener confirmationDialogActionListener;

    // Layout inflater - needed to inflate views within Dialog
    private LayoutInflater layoutInflater;
    private Context context;
    private Dialog alertDialog;

    /**
     * Class constructor
     *
     * @param layoutInflater - layout inflater used to inflate Dialog inner views
     * @param context        - Context used within
     */
    public ConfirmationDialog(LayoutInflater layoutInflater, Context context) {

        this.layoutInflater = layoutInflater;
        this.context = context;
    }

    /**
     * Initialize this alert dialog
     *
     * @param viewGroup   view group used when inflating body view
     * @param titleString String used as title for this alert dialog
     */
    public void initialize(@NotNull ViewGroup viewGroup, String titleString) {
        final Title alertDialogTitle = new Title();
        final Body alertDialogBody = new Body();

        View titleView = layoutInflater.inflate(R.layout.alertdialolg_title, null);
        ButterKnife.bind(alertDialogTitle, titleView);
        alertDialogTitle.valueTV.setText(titleString);

        View bodyView = layoutInflater.inflate(R.layout.alertdialog_yesnobody, viewGroup, false);
        ButterKnife.bind(alertDialogBody, bodyView);

        AlertDialog.Builder builder = new AlertDialog.Builder(context).setCustomTitle(titleView);

        builder.setView(bodyView);
        alertDialog = builder.create();
    }

    public void setConfirmationDialogActionListener(ConfirmationDialogActionListener confirmationDialogActionListener) {
        this.confirmationDialogActionListener = confirmationDialogActionListener;
    }

    /**
     * Show custom built alert dialog
     */
    public void show() {
        alertDialog.show();
    }
}
