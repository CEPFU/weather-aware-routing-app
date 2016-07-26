package com.example.weatherrouting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.mapbox.services.android.geocoder.ui.GeocoderAutoCompleteView;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.geocoding.v5.GeocodingCriteria;
import com.mapbox.services.geocoding.v5.models.CarmenFeature;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RouteConfigurationActivity extends AppCompatActivity implements DownloadCompleteListener
{
    private GeocoderAutoCompleteView fromView;
    private GeocoderAutoCompleteView toView;

    private Position fromPosition;
    private Position toPosition;
    private String valueType;
    private String valueBound;
    private double value;
    private String valueBlock;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_configuration);

        fromView = (GeocoderAutoCompleteView) findViewById(R.id.search_from);
        fromView.setAccessToken(getResources().getString(R.string.access_token));
        fromView.setType(GeocodingCriteria.TYPE_PLACE);
        fromView.setOnFeatureListener(new GeocoderAutoCompleteView.OnFeatureListener() {
            @Override
            public void OnFeatureClick(CarmenFeature feature) {
                fromPosition = feature.asPosition();
            }
        });


        toView = (GeocoderAutoCompleteView) findViewById(R.id.search_to);
        toView.setAccessToken(getResources().getString(R.string.access_token));
        toView.setType(GeocodingCriteria.TYPE_PLACE);
        toView.setOnFeatureListener(new GeocoderAutoCompleteView.OnFeatureListener() {
            @Override
            public void OnFeatureClick(CarmenFeature feature) {
                toPosition = feature.asPosition();
            }
        });

        Spinner valueSpinner = (Spinner) findViewById(R.id.spinner_value_type);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> valuesAdapter = ArrayAdapter.createFromResource(this,
                R.array.search_weather_values, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        valuesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        assert valueSpinner != null;
        valueSpinner.setAdapter(valuesAdapter);
        valueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                        valueType = "WEATHER_TEMPERATURE";
                        break;
                    case 1:
                        valueType = "WEATHER_TEMPERATURE_HIGH";
                        break;
                    case 2:
                        valueType = "WEATHER_TEMPERATURE_LOW";
                        break;
                    case 3:
                        valueType = "WEATHER_WINDCHILL";
                        break;
                    case 4:
                        valueType = "WEATHER_PRECIPITATION_DEPTH";
                        break;
                    case 5:
                        valueType = "WEATHER_MAXIMUM_WIND_SPEED";
                        break;
                    case 6:
                        valueType = "WEATHER_CLOUDAGE";
                        break;
                    case 7:
                        valueType = "WEATHER_ATMOSPHERIC_HUMIDITY";
                        break;
                    case 8:
                        valueType = "WEATHER_ATMOSPHERIC_PRESSURE";
                        break;
                    case 9:
                        valueType = "WEATHER_SUNSHINE_DURATION";
                        break;
                    case 10:
                        valueType = "WEATHER_SNOW_HEIGHT";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                valueType = null;
            }
        });


        Spinner boundSpinner = (Spinner) findViewById(R.id.spinner_value_bound);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> boundAdapter = ArrayAdapter.createFromResource(this,
                R.array.search_value_bound, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        boundAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        assert boundSpinner != null;
        boundSpinner.setAdapter(boundAdapter);
        boundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                        valueBound = "lower";
                        break;
                    case 1:
                        valueBound = "upper";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                valueBound = null;
            }
        });

        EditText valueText = (EditText) findViewById(R.id.edit_value);
        assert valueText != null;
        valueText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                value = Double.valueOf(s.toString());
            }
        });


        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        assert radioGroup != null;
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_block)
                {
                    valueBlock = "block";
                }
                else
                {
                    valueBlock = "noblock";
                }

            }
        });

        Button calcButton = (Button) findViewById(R.id.route_calculate_button);
        assert calcButton != null;
        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
//                StringBuilder urlBuilder = new StringBuilder();
//                urlBuilder.append("http://server:port/route");
//                urlBuilder.append("?point").append(fromPosition.getLatitude()).append(",").append(fromPosition.getLongitude());
//                urlBuilder.append("&point").append(toPosition.getLatitude()).append(",").append(toPosition.getLongitude());
//                urlBuilder.append("&type=json&vehicle=car&locale=de-DE");
//                urlBuilder.append("&weighting=probabilistic");
//                urlBuilder.append("&elevation=false&ch.disable=true");
//                urlBuilder.append("&algorithm=astar");
//                urlBuilder.append("&user_value=").append(value);
//                urlBuilder.append("&user_value_type=").append(valueType);
//                urlBuilder.append("&user_value_bound").append(valueBound);
//                urlBuilder.append("&user_value_block").append(valueBlock);
//                urlBuilder.append("&user_time=").append(System.currentTimeMillis() / 1000);
//
//                makeRequest(urlBuilder.toString());

                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void downloadComplete(String json)
    {

    }

    private void makeRequest(String url)
    {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String result = response.body().string();

                RouteConfigurationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            downloadComplete(result);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
