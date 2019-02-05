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

public class CustomAlertDialog {
    public interface CustomDialogActionListener {
        public void onOption1(int dialogType);
        public void onOption2(int dialogType);
    }

    class Title {
        @BindView(R.id.alertdialog_title)
        TextView valueTV;
    }

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

    private int dialogType;
    private LayoutInflater layoutInflater;
    private Context context;
    private Dialog alertDialog;
    private CustomDialogActionListener customDialogActionListener;

    public CustomAlertDialog(int dialogType, LayoutInflater layoutInflater, Context context) {
        this.dialogType = dialogType;
        this.layoutInflater = layoutInflater;
        this.context = context;
    }

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

    public void show() {
        alertDialog.show();
    }
}