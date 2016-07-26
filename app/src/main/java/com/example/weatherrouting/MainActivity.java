package com.example.weatherrouting;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.geocoder.ui.GeocoderAutoCompleteView;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.geocoding.v5.GeocodingCriteria;
import com.mapbox.services.geocoding.v5.models.CarmenFeature;

public class MainActivity extends AppCompatActivity
{
    private static final String LOG_TAG = "MainActivity";

    private MapView mapView;
    private MapboxMap map;

    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.mapview);
        assert mapView != null;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
            }
        });
        GeocoderAutoCompleteView autoComplete = (GeocoderAutoCompleteView) findViewById(R.id.searchview);
        assert autoComplete != null;
        autoComplete.setAccessToken(getResources().getString(R.string.access_token));
        autoComplete.setType(GeocodingCriteria.MODE_PLACES);
        autoComplete.setOnFeatureListener(new GeocoderAutoCompleteView.OnFeatureListener() {
            @Override
            public void OnFeatureClick(CarmenFeature feature) {
                Position position = feature.asPosition();
                placeMarkerAndFlyThere(position.getLatitude(), position.getLongitude());
            }
        });

        FloatingActionButton routeConfigButton = (FloatingActionButton) findViewById(R.id.fab_navigation);
        assert routeConfigButton != null;
        routeConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RouteConfigurationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void placeMarkerAndFlyThere(double latitude, double longitude)
    {
        map.clear();
        map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Gesuchter Ort"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(15)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mapView.onDestroy();
    }
}
