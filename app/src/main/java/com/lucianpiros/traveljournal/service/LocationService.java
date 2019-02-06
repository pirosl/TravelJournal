package com.lucianpiros.traveljournal.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class LocationService implements LocationListener {
    private static LocationService locationService = null;
    private static boolean initialized = false;

    protected LocationManager locationManager;
    private double latitude;
    private double longitude;

    private LocationService() {
    }

    public static LocationService getInstance() {
        if (locationService == null) {
            locationService = new LocationService();
        }

        return locationService;
    }

    public void setActivity(Activity activity) {
        if(!initialized) {
            locationService.locationManager = (LocationManager) (activity.getSystemService(Context.LOCATION_SERVICE));
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        0);
            }
            locationService.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationService);
            initialized = true;
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
