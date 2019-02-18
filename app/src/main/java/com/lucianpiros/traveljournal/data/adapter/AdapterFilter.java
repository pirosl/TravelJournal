package com.lucianpiros.traveljournal.data.adapter;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.List;

/**
 * Filter to be used in adapter
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class AdapterFilter {
    public static final int FILTERTYPE_DATE = 1;
    public static final int FILTERTYPE_GEOFENCE = 2;
    public static final int FILTERTYPE_LIST = 2;

    private boolean isFiltered;
    private int filterType;
    private Calendar calendar;
    private LatLng latLng;
    private String adventureKey;

    public AdapterFilter() {
        isFiltered = false;
    }

    public boolean isFiltered() {
        return isFiltered;
    }

    public void setFiltered(boolean filtered) {
        isFiltered = filtered;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getAdventureKeys() {
        return adventureKey;
    }

    public void setAdventureKeys(String adventureKey) {
        this.adventureKey = adventureKey;
    }
}
