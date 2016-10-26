package com.example.weatherrouting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ResultDetailActivity extends AppCompatActivity
{
    public static final String JSON_EXTRA = "com.example.weatherrouting.JSON_EXTRA";

    private ListView listView;

    private String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_detail);

        jsonString = getIntent().getStringExtra(JSON_EXTRA);

        listView = (ListView) findViewById(R.id.instruction_list_view);
        List<String> instructionList = new ArrayList<>();
        try
        {
//            InputStream inputStream = getAssets().open("route.json");
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
//            StringBuilder builder = new StringBuilder();
//            int cp;
//            while ((cp = reader.read()) != -1) {
//                builder.append((char) cp);
//            }
//
//            inputStream.close();

            JSONObject json = new JSONObject(jsonString);
            JSONArray paths = json.getJSONArray("paths");
            JSONObject path = paths.getJSONObject(0);
            JSONArray instructions = path.getJSONArray("instructions");
            for (int arrayIndex = 0; arrayIndex < instructions.length(); arrayIndex++)
            {
                JSONObject instruction = instructions.getJSONObject(arrayIndex);
                instructionList.add(instruction.getString("text"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        String[] listItems = new String[instructionList.size()];
        for (int i = 0; i < instructionList.size(); i++)
        {
            listItems[i] = instructionList.get(i);
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);

    }
}
