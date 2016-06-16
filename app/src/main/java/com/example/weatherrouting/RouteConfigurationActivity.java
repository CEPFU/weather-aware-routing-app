package com.example.weatherrouting;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.mapbox.geocoder.service.models.GeocoderFeature;

public class RouteConfigurationActivity extends AppCompatActivity
{
    private AutoCompleteTextView fromView;
    private AutoCompleteTextView toView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_configuration);

        final GeocoderAdapter adapter = new GeocoderAdapter(this);

        fromView = (AutoCompleteTextView) findViewById(R.id.search_from);
        TextDrawable fromDrawable = TextDrawable.builder().buildRect("From", Color.BLACK);
        fromView.setCompoundDrawablesWithIntrinsicBounds(fromDrawable, null, null, null);
        fromView.setLines(1);
        fromView.setAdapter(adapter);
        fromView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                GeocoderFeature feature = adapter.getItem(position);
                fromView.setText(feature.getText());
                // hide keyboard
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
            }
        });
        // Make view clearable
        final Drawable clearButton = getResources().getDrawable(R.drawable.ic_action_navigation_close);
        fromView.setCompoundDrawablesWithIntrinsicBounds(null, null, clearButton, null);
        fromView.setOnTouchListener(new View.OnTouchListener()
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
                    fromView.setText("");
                }
                return false;
            }
        });


        toView = (AutoCompleteTextView) findViewById(R.id.search_to);
        toView.setCompoundDrawablesWithIntrinsicBounds(TextDrawable.builder().buildRect(getResources().getString(R.string.search_to_text), Color.BLACK), null, null, null);
        toView.setLines(1);
        toView.setAdapter(adapter);
        toView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                GeocoderFeature feature = adapter.getItem(position);
                toView.setText(feature.getText());
                // hide keyboard
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
            }
        });
        toView.setCompoundDrawablesWithIntrinsicBounds(null, null, clearButton, null);
        toView.setOnTouchListener(new View.OnTouchListener()
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
                    toView.setText("");
                }
                return false;
            }
        });
    }
}
