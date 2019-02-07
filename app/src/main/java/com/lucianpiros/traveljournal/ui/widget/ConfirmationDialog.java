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

public class ConfirmationDialog {

    public interface ConfirmationDialogActionListener {
        void onAccept();
        void onDecline();
    }

    class Title {
        @BindView(R.id.alertdialog_title)
        TextView valueTV;
    }

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

    private ConfirmationDialogActionListener confirmationDialogActionListener;

    private LayoutInflater layoutInflater;
    private Context context;
    private Dialog alertDialog;

    public ConfirmationDialog(LayoutInflater layoutInflater, Context context) {

        this.layoutInflater = layoutInflater;
        this.context = context;
    }

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

    public void show() {
        alertDialog.show();
    }
}
