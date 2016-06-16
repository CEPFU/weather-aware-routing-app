package com.example.weatherrouting;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.mapbox.geocoder.service.models.GeocoderFeature;
import com.mapbox.mapboxsdk.annotations.BaseMarkerOptions;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
{
    private static final String LOG_TAG = "MainActivity";

    private MapView mapView;
    private AutoCompleteTextView autoCompleteTextView;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setStyleUrl(Style.getMapboxStreetsUrl(9));
        mapView.onCreate(savedInstanceState);

        final GeocoderAdapter adapter = new GeocoderAdapter(this);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.searchview);
        autoCompleteTextView.setLines(1);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                GeocoderFeature feature = adapter.getItem(position);
                autoCompleteTextView.setText(feature.getText());
                // hide keyboard
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
                placeMarkerAndFlyThere(feature);
            }
        });
        // Make view clearable
        final Drawable clearButton = getResources().getDrawable(R.drawable.ic_action_navigation_close);
        autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, clearButton, null);
        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                AutoCompleteTextView tv = (AutoCompleteTextView) v;
                if(tv.getCompoundDrawables()[2] == null)
                {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP)
                {
                    return false;
                }
                if (event.getX() > tv.getWidth() - tv.getPaddingRight() - clearButton.getIntrinsicWidth())
                {
                    autoCompleteTextView.setText("");
                }
                return false;
            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_navigation);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent routeConfigurationIntent = new Intent(getApplicationContext(), RouteConfigurationActivity.class);
                startActivity(routeConfigurationIntent);
            }
        });
    }

    private void placeMarkerAndFlyThere(final GeocoderFeature feature)
    {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.clear();
                mapboxMap.addMarker(new MarkerOptions()
                .title(feature.getText())
                .position(new LatLng(feature.getLatitude(), feature.getLongitude())));

                // Fly to new position
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(feature.getLatitude(), feature.getLongitude()))
                        .zoom(13)
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);
            }
        });
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
