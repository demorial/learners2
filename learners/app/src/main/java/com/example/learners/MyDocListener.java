package com.example.learners;
import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyDocListener extends MapsActivity implements LocationSource.OnLocationChangedListener {
    private GoogleMap mMap;
    public void onLocationChanged(Location location)
    {
        if(location!=null)
        {
            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        }
    }}