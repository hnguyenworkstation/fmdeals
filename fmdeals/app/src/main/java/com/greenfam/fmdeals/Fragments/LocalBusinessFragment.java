package com.greenfam.fmdeals.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.greenfam.fmdeals.Helpers.PermissionUtils;
import com.greenfam.fmdeals.R;

/**
 * Created by quang on 8/31/2016.
 */
public class LocalBusinessFragment extends Fragment
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener {

    private MapView mMapView;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap googleMap;
    private View rootView;
    private double currentLat;
    private double currentLong;

    public LocalBusinessFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();

            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.local_business_fragment, container, false);
            enableMyLocation();
            switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) )
            {
                case ConnectionResult.SUCCESS:
                    Toast.makeText(getActivity(), "SUCCESS", Toast.LENGTH_SHORT).show();
                    mMapView = (MapView) rootView.findViewById(R.id.map);
                    mMapView.onCreate(savedInstanceState);

                    mMapView.onResume(); // needed to get the map to display immediately
                    mMapView.getMapAsync(this);
                    break;
                case ConnectionResult.SERVICE_MISSING:
                    Toast.makeText(getActivity(), "SERVICE MISSING", Toast.LENGTH_SHORT).show();
                    break;
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                    Toast.makeText(getActivity(), "UPDATE REQUIRED", Toast.LENGTH_SHORT).show();
                    break;
                default: Toast.makeText(getActivity(), GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_SHORT).show();
            }

        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap map){
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnCameraMoveStartedListener(this);
        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnCameraMoveCanceledListener(this);

        // For showing a move to my location button
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
            return;
        }

        googleMap.setMyLocationEnabled(true);

        // For dropping a marker at a point on the Map
        initMapMove();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation(){
            System.out.print("get here");
            if(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
                // Permission to access the location is missing.
                PermissionUtils.requestPermission(this.getActivity(),LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,true);
            }else if(googleMap!=null){
                // Access to the location has been granted to the app.
                googleMap.setMyLocationEnabled(true);
            }
    }

    @Override
    public boolean onMyLocationButtonClick(){
            Toast.makeText(this.getContext(),"MyLocation button clicked",Toast.LENGTH_SHORT).show();
            // Return false so that we don't consume the event and the default behavior still occurs
            // (the camera animates to the user's current position).
            return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getFragmentManager(), "dialog");
    }

    private void initMapMove() {
        googleMap.clear();

        //Creating a location object
        Location location = googleMap.getMyLocation();
        if (location != null) {

            currentLat = location.getLatitude();
            currentLong = location.getLongitude();

            //String to display current latitude and longitude
            String msg = currentLat + ", "+currentLong;

            //Creating a LatLng Object to store Coordinates
            LatLng latLng = new LatLng(currentLat, currentLat);

            //Adding marker to map
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng) //setting position
                    .draggable(true) //Making the marker draggable
                    .title("Current Location")); //Adding a title

            //Moving the camera
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            //Animating the camera
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));

            //Displaying current coordinates in toast
            Toast.makeText(this.getContext(), msg, Toast.LENGTH_LONG).show();
        } else {
            String msg = "Temporary Coordinate!";
            //Creating a LatLng Object to store Coordinates
            LatLng latLng = new LatLng(10.8231, 106.6297);

            //Adding marker to map
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng) //setting position
                    .draggable(true) //Making the marker draggable
                    .title("Current Location")); //Adding a title

            //Moving the camera
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            //Animating the camera
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));

            //Displaying current coordinates in toast
            Toast.makeText(this.getContext(), msg, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


    @Override
    public void onCameraIdle(){

    }

    @Override
    public void onCameraMoveCanceled(){

    }

    @Override
    public void onCameraMove(){

    }

    @Override
    public void onCameraMoveStarted(int i){

    }
}
