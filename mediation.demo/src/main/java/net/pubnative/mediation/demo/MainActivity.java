package net.pubnative.mediation.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity
{
    private final static String APP_TOKEN = "bed5dfea7feb694967a8755bfa7f67fdf1ceb9c291ddd7b8825983a103c8b266";

    private static AdListAdapter adListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (adListAdapter == null)
        {
            List<CellRequestModel> requests = new ArrayList<>();
            requests.add(new CellRequestModel(APP_TOKEN, "facebook_only"));
            requests.add(new CellRequestModel(APP_TOKEN, "pubnative_only"));
            requests.add(new CellRequestModel(APP_TOKEN, "yahoo_only"));
            requests.add(new CellRequestModel(APP_TOKEN, "waterfall"));
            requests.add(new CellRequestModel(APP_TOKEN, "imp_day_cap_10"));
            requests.add(new CellRequestModel(APP_TOKEN, "imp_hour_cap_10"));
            requests.add(new CellRequestModel(APP_TOKEN, "pacing_cap_hour_1"));
            requests.add(new CellRequestModel(APP_TOKEN, "pacing_cap_min_1"));
            requests.add(new CellRequestModel(APP_TOKEN, "disabled"));

            adListAdapter = new AdListAdapter(this, R.layout.ad_list_cell, requests);
        }

        ListView listView = (ListView) findViewById(R.id.ad_list);
        if (listView != null)
        {
            listView.setAdapter(adListAdapter);
        }
    }
}
