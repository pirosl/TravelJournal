package com.lucianpiros.traveljournal.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.SphericalUtil;
import com.lucianpiros.traveljournal.R;
import com.lucianpiros.traveljournal.data.DataCache;
import com.lucianpiros.traveljournal.model.Note;
import com.lucianpiros.traveljournal.service.LocationService;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Map fragment. Displays a map overlayed with icons at geolocations where notes were created
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class MapFragment extends Fragment {
    private final static String TAG = MapFragment.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;

    private final int ZOOM_LEVELS = 15;

    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.fragment_layout)
    ConstraintLayout fragmentLayout;
    private GoogleMap googleMap;
    private float zoomFactor;
    private float widths[];
    private float heights[];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        ButterKnife.bind(this, rootView);

        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_LOCATION);

                    return;
                }

                mapSetup();
                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        //  float zoomLevel=googleMap.getCameraPosition().zoom;
                        Snackbar.make(fragmentLayout, "Zoom factor" + zoomFactor, Snackbar.LENGTH_SHORT).show();
                        addMapOverlay();
                    }
                });
                addMapOverlay();
            }
        });


        widths = new float[ZOOM_LEVELS];
        heights = new float[ZOOM_LEVELS];

        TypedArray widthTA = getResources().obtainTypedArray(R.array.widths);
        TypedArray heightTA = getResources().obtainTypedArray(R.array.heights);

        for (int i = 0; i < ZOOM_LEVELS; i++) {
            widths[ZOOM_LEVELS - 1 - i] = widthTA.getFloat(i, 0.0f);
            heights[ZOOM_LEVELS - 1 - i] = heightTA.getFloat(i, 0.0f);
        }

        return rootView;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapSetup();
                    addMapOverlay();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @SuppressLint("MissingPermission")
    private void mapSetup() {
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        LocationService locationService = LocationService.getInstance();
        LatLng here = new LatLng(locationService.getLatitude(), locationService.getLongitude());

        googleMap.setOnGroundOverlayClickListener(new GoogleMap.OnGroundOverlayClickListener() {
            @Override
            public void onGroundOverlayClick(GroundOverlay groundOverlay) {
                LatLng latLng = groundOverlay.getPosition();
                Snackbar.make(fragmentLayout, "Messages at" + latLng.latitude + " " + latLng.longitude, Snackbar.LENGTH_SHORT).show();
            }
        });

        CameraPosition cameraPosition = new CameraPosition.Builder().target(here).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private void addMapOverlay() {
        zoomFactor = googleMap.getCameraPosition().zoom;
        new SetupMapTask().execute("");
    }

    /**
     * AsyncTask class used to add ground overlays
     *
     * @author Lucian Piros
     * @version 1.0
     */
    private class SetupMapTask extends AsyncTask<String, Void, String> {

        private List<LatLng> geofences;

        protected SetupMapTask() {
            geofences = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... params) {

            List<Note> notes = DataCache.getInstance().getNotesList();

            if (notes != null) {
                for (Note note : notes) {
                    if (!isNextTo(note)) {
                        geofences.add(new LatLng(note.getLatitude(), note.getLongitude()));
                    }
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            for (LatLng latLng : geofences) {
                GroundOverlayOptions groundOverlay;

                Drawable drawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_notes);
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.OVERLAY);
                drawable.draw(canvas);

                int zoomFactorI = (int) zoomFactor;
                if (zoomFactorI >= ZOOM_LEVELS)
                    zoomFactorI = ZOOM_LEVELS - 1;
                groundOverlay = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .position(latLng, heights[zoomFactorI], widths[zoomFactorI])
                        .clickable(true);

                googleMap.addGroundOverlay(groundOverlay);
            }
        }

        private boolean isNextTo(Note n) {
            LatLng noteLatLng = new LatLng(n.getLatitude(), n.getLongitude());
            for (LatLng latLng : geofences) {
                double distance = SphericalUtil.computeDistanceBetween(noteLatLng, latLng);
                if (distance < 1000 * (15 / zoomFactor)) {
                    return true;
                }
            }

            return false;
        }
    }
}