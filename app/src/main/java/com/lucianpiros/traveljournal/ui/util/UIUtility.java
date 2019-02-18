package com.lucianpiros.traveljournal.ui.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.annotations.NotNull;
import com.lucianpiros.traveljournal.R;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.viewpager.widget.ViewPager;

/**
 * Utility method class
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class UIUtility {

    private static ViewPager viewPager;

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

    public static ViewPager getViewPager() {
        return viewPager;
    }

    public static void setViewPager(ViewPager viewPager) {
        UIUtility.viewPager = viewPager;
    }

    /**
     * Change tint color for menu item
     *
     * @param menu    - menu on which color should be changed
     * @param menuRid = menu resource id
     */
    public static void changeTintColor(Menu menu, int menuRid, Context context) {
        MenuItem menuItem = menu.findItem(menuRid);
        if(menuItem != null) {
            Drawable drawable = menuItem.getIcon();
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.colorAccent));
            menuItem.setIcon(drawable);
        }
    }

}
