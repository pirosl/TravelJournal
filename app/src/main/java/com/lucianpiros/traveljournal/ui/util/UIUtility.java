package com.lucianpiros.traveljournal.ui.util;

import android.widget.TextView;

import com.google.firebase.database.annotations.NotNull;

/**
 * Utility method class
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class UIUtility {

    /**
     * Returns true if passed in TextView contains information
     *
     * @param textView - TextView to be checked
     * @return - true if TextView contains info, false otherwise
     */
    public static boolean isValid(@NotNull TextView textView) {
        CharSequence text = textView.getText();

        return (text != null && text.toString().length() > 0);
    }
}
