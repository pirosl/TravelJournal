package com.lucianpiros.traveljournal.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

/**
 * LocationService class. Listen for device location changes and provides device latitude and
 * longitude to any interested client. Used to geotag journal notes
 *
 * <p>
 * This class is a singleton class.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class LocationService implements LocationListener {
    private static final String TAG = LocationService.class.getSimpleName();

    // static private class reference
    private static LocationService locationService = null;

    private static boolean initialized = false;

    // LocationManager instance
    private LocationManager locationManager;
    private double latitude;
    private double longitude;

    /**
     * Private class constructor
     */
    private LocationService() {
        // default location Sydney
        latitude = -34;
        longitude = 151;
    }

    /**
     * Retuns singleton instance
     *
     * @return singleton instance
     */
    public static LocationService getInstance() {
        if (locationService == null) {
            locationService = new LocationService();
        }

        return locationService;
    }

    /**
     * Set necesary information to be able to get location
     *
     * @param activity Activity to which location manager will be tight
     */
    public void setActivity(Activity activity) {
        if (!initialized) {
            locationService.locationManager = (LocationManager) (activity.getSystemService(Context.LOCATION_SERVICE));
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        0);
                return;
            }
            if (locationService.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
                locationService.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationService);
                initialized = true;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
