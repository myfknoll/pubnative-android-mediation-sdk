package net.pubnative.mediation.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity
{
    private final static String APP_TOKEN = "7c26af3aa5f6c0a4ab9f4414787215f3bdd004f80b1b358e72c3137c94f5033c";

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
