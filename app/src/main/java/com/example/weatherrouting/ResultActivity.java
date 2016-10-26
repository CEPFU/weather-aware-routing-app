package com.example.weatherrouting;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity implements OnMapReadyCallback
{
    public static final String JSON_EXTRA = "com.example.weatherrouting.JSON_EXTRA";
    private static final String LOG_TAG = "ResultActivity";

    private MapView mapView;
    private MapboxMap mapboxMap;

    private String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        jsonString = getIntent().getStringExtra(JSON_EXTRA);

        mapView = (MapView) findViewById(R.id.resultmapview);
        mapView.setStyleUrl("mapbox://styles/mapbox/light-v9");
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        FloatingActionButton notificationButton = (FloatingActionButton) findViewById(R.id.fab_notification);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResultDetailActivity.class);
                intent.putExtra(ResultDetailActivity.JSON_EXTRA, jsonString);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)
    {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(JSON_EXTRA, jsonString);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap)
    {
        this.mapboxMap = mapboxMap;

        new DrawGeoJSON(jsonString).execute();
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

    private class DrawGeoJSON extends AsyncTask<Void, Void, List<LatLng>>
    {
        private String json;

        DrawGeoJSON(String json)
        {
            this.json = json;
        }

        @Override
        protected List<LatLng> doInBackground(Void... params)
        {
            List<LatLng> points = new ArrayList<>();

            try
            {
//                InputStream inputStream = getAssets().open("route.json");
//                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
//                StringBuilder builder = new StringBuilder();
//                int cp;
//                while ((cp = reader.read()) != -1)
//                {
//                    builder.append((char) cp);
//                }
//
//                inputStream.close();

                JSONObject json = new JSONObject(this.json);
                JSONArray paths = json.getJSONArray("paths");
                JSONObject path = paths.getJSONObject(0);
                JSONObject jsonPoints = path.getJSONObject("points");
                JSONArray coordinates = jsonPoints.getJSONArray("coordinates");
                for (int lc = 0; lc < coordinates.length(); lc++)
                {
                    JSONArray coord = coordinates.getJSONArray(lc);
                    LatLng latLng = new LatLng(coord.getDouble(1), coord.getDouble(0));
                    points.add(latLng);
                }
            }
            catch (Exception e)
            {
                Log.e(LOG_TAG, "Exception reading JSON: " + e);
            }
            return points;
        }

        @Override
        protected void onPostExecute(List<LatLng> points)
        {
            LatLng[] pointsArray = points.toArray(new LatLng[points.size()]);

            if (points.size() > 0)
            {
                PolylineOptions options = new PolylineOptions()
                        .add(pointsArray)
                        .color(Color.parseColor("#3bb2d0"))
                        .width(2);

                mapboxMap.addPolyline(options);

                mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds.Builder().include(points.get(0)).include(points.get(points.size() - 1)).build(), 50), 1000);
            }
        }
    }
}
